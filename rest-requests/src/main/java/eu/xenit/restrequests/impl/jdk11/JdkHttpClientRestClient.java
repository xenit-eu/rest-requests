package eu.xenit.restrequests.impl.jdk11;

import eu.xenit.restrequests.api.ReactiveRestClient;
import eu.xenit.restrequests.api.converter.HttpBodyConverter;
import eu.xenit.restrequests.api.converter.HttpBodyConverter.DeserializationContext;
import eu.xenit.restrequests.api.converter.HttpBodyConverter.SerializationContext;
import eu.xenit.restrequests.api.exceptions.HttpTimeoutException;
import eu.xenit.restrequests.api.filters.ReactiveRestClientFilter;
import eu.xenit.restrequests.api.http.HttpHeaders;
import eu.xenit.restrequests.api.http.MediaType;
import eu.xenit.restrequests.impl.ConverterProcessor;
import eu.xenit.restrequests.impl.FilterProcessor;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.ResponseInfo;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;

class JdkHttpClientRestClient implements ReactiveRestClient {

    private final HttpClient client;
    private final ConverterProcessor converterProcessor;
    private final FilterProcessor defaultFilters;

    JdkHttpClientRestClient(HttpClient httpClient, Collection<HttpBodyConverter> httpBodyConverters,
            Collection<ReactiveRestClientFilter> filters) {
        this.client = httpClient;
        this.converterProcessor = new ConverterProcessor(httpBodyConverters);
        this.defaultFilters = new FilterProcessor(filters);
    }

    public static JdkHttpClientRestClient newClient() {
        return builder().build();
    }

    public static JdkHttpClientBuilder builder() {
        return new JdkHttpClientBuilder();
    }

    @Override
    public RequestSpecification get(URI uri) {
        return new JdkHttpRequestSpecification(this.client, "GET", uri, this.converterProcessor);
    }

    @Override
    public RequestSpecification put(URI uri) {
        return new JdkHttpRequestSpecification(this.client, "PUT", uri, this.converterProcessor);
    }

    @Override
    public RequestSpecification post(URI uri) {
        return new JdkHttpRequestSpecification(this.client, "POST", uri, this.converterProcessor);
    }

    @Override
    public RequestSpecification delete(URI uri) {
        return new JdkHttpRequestSpecification(this.client, "DELETE", uri, this.converterProcessor);
    }


    private static class JdkHttpRequestSpecification implements RequestSpecification {

        private final HttpClient client;
        private final String method;
        private final URI uri;

        private final HttpRequest.Builder builder;
        private final ConverterProcessor converterProcessor;

        private HttpRequest.BodyPublisher bodyHandler = HttpRequest.BodyPublishers.noBody();

        private MediaType _cachedAcceptMediaType;

        private JdkHttpRequestSpecification(HttpClient client, String method, URI uri, ConverterProcessor converterProcessor) {
            this.client = Objects.requireNonNull(client, "client cannot be null");
            this.method = Objects.requireNonNull(method, "method cannot be null");
            this.uri = Objects.requireNonNull(uri, "uri cannot be null");
            this.converterProcessor = Objects.requireNonNull(converterProcessor, "objectMapping cannot be null");

            this.builder = HttpRequest.newBuilder(uri);

        }

        @Override
        public RequestSpecification header(String name, String... values) {
            Objects.requireNonNull(name, "name cannot be null");
            Arrays.stream(values).forEach(value -> this.builder.header(name, value));
            return this;
        }


        @Override
        public RequestSpecification accept(MediaType mediaType) {
            this._cachedAcceptMediaType = mediaType;
            this.builder.header(HttpHeaders.HeaderNames.ACCEPT, mediaType.toString());
            return this;
        }

        public MediaType accept() {
            return this._cachedAcceptMediaType;
        }

        @Override
        public <TRequestBody> RequestSpecification body(TRequestBody data) {
            var bytes = this.converterProcessor.write(new JdkHttpClientSerializationContext(data, this::accept));
            this.bodyHandler = HttpRequest.BodyPublishers.ofByteArray(bytes);
            return this;
        }

        @Override
        public RequestSpecification timeout(Duration timeout) {
            this.builder.timeout(timeout);
            return this;
        }

        @Override
        public URI uri() {
            return this.uri;
        }

        @Override
        public String method() {
            return this.method;
        }

        @Override
        public ResponseSpecification<Void> response() {

            final HttpRequest request = this.builder.method(this.method, this.bodyHandler).build();

            return new JdkHttpClientResponseSpec<>(this.client, request, HttpResponse.BodyHandlers.discarding(), this.converterProcessor);
        }
    }

    private static class JdkHttpClientSerializationContext implements SerializationContext {
        private final Object source;
        private final Supplier<MediaType> mediaTypeSupplier;

        private JdkHttpClientSerializationContext(Object source, Supplier<MediaType> mediaTypeSupplier) {
            this.source = source;
            this.mediaTypeSupplier = mediaTypeSupplier;
        }

        @Override
        public Object getSource() {
            return this.source;
        }

        @Override
        public MediaType getContentType() {
            return this.mediaTypeSupplier.get();
        }
    }

    private static class JdkHttpClientResponseSpec<TResponse>
            implements ResponseSpecification<TResponse> {

        private final HttpClient client;
        private final HttpRequest request;
        private final BodyHandler<TResponse> bodyHandler;
        private final ConverterProcessor converterProcessor;

        private JdkHttpClientResponseSpec(HttpClient client, HttpRequest request,
                BodyHandler<TResponse> bodyHandler, ConverterProcessor converterProcessor) {
            this.client = client;
            this.request = request;
            this.bodyHandler = bodyHandler;
            this.converterProcessor = converterProcessor;
        }

        @Override
        public <NewResponse> ResponseSpecification<NewResponse> body(Class<NewResponse> type) {
            return new JdkHttpClientResponseSpec<>(this.client, this.request, new BodyHandler<NewResponse>() {

                @Override
                public BodySubscriber<NewResponse> apply(ResponseInfo responseInfo) {

                    // HttpStatusExceptionFilter should be invoked here ?

                    BodySubscriber<byte[]> upstream = HttpResponse.BodySubscribers.ofByteArray();
                    return HttpResponse.BodySubscribers.mapping(upstream, (byteArray) -> {
                        var context = new JdkResponseInfoDeserializationContext(responseInfo, byteArray);
                        return converterProcessor.read(context, type);
                    });
                }
            }, this.converterProcessor);
        }

        @Override
        public CompletableFuture<RestResponse<TResponse>> execute() {

            return this.client.sendAsync(request, bodyHandler)
                    .exceptionally(throwable -> {
                        // unwrap the CompletionException
                        if (throwable instanceof CompletionException) {
                            throwable = throwable.getCause();
                        }

                        if (throwable instanceof java.net.http.HttpTimeoutException) {
                            throw new HttpTimeoutException(throwable);
                        }

                        throw new AssertionError(throwable);
                    })
                    .thenApply(JdkHttpClientResponse::new);

        }

        private static class JdkResponseInfoDeserializationContext implements DeserializationContext {

            private final ResponseInfo responseInfo;
            private final byte[] body;

            public JdkResponseInfoDeserializationContext(ResponseInfo responseInfo, byte[] body) {
                this.responseInfo = responseInfo;
                this.body = body;
            }

            @Override
            public MediaType getContentType() {
                return this.responseInfo.headers()
                        .firstValue(HttpHeaders.HeaderNames.CONTENT_TYPE)
                        .map(MediaType::parseMediaType)
                        .orElse(null);
            }

            @Override
            public byte[] getSource() {
                return this.body;
            }
        }
    }

    private static class JdkHttpClientResponse<TResponse> implements RestResponse<TResponse> {


        private final HttpResponse<TResponse> response;

        private JdkHttpClientResponse(HttpResponse<TResponse> httpResponse) {
            this.response = httpResponse;
        }

        @Override
        public TResponse body() {
            return response.body();
        }

        public HttpHeaders headers() {
            return new JdkHttpHeaders(response.headers());
        }

        @Override
        public int statusCode() {
            return this.response.statusCode();
        }

        private static class JdkHttpHeaders implements HttpHeaders {

            private final java.net.http.HttpHeaders headers;

            public JdkHttpHeaders(java.net.http.HttpHeaders headers) {
                Objects.requireNonNull(headers, "headers cannot be null");
                this.headers = headers;
            }

            @Override
            public List<String> get(String headerName) {
                Objects.requireNonNull(headerName, "headerName cannot be null");
                return this.headers.allValues(headerName);
            }
        }
    }

}
