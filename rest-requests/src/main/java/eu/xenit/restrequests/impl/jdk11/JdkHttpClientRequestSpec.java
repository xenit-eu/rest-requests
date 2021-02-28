package eu.xenit.restrequests.impl.jdk11;

import eu.xenit.restrequests.api.http.HttpHeaders;
import eu.xenit.restrequests.api.http.MediaType;
import eu.xenit.restrequests.api.reactive.ReactiveRequestSpec;
import eu.xenit.restrequests.api.reactive.ReactiveResponseSpec;
import eu.xenit.restrequests.impl.jdk11.JdkResponseBodyHandler.DiscardingJdkResponseBodyHandler;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

class JdkHttpClientRequestSpec implements ReactiveRequestSpec {

    private final JdkHttpClientRestClient client;
    private final JdkHttpContext httpContext;

    private final String method;
    private final URI uri;

    private final HttpRequest.Builder builder;

    private HttpRequest.BodyPublisher requestBodyHandler = HttpRequest.BodyPublishers.noBody();

    private MediaType _cachedAcceptMediaType;

    JdkHttpClientRequestSpec(JdkHttpClientRestClient client, String method, URI uri) {
        this.client = Objects.requireNonNull(client, "client cannot be null");

        this.method = Objects.requireNonNull(method, "method cannot be null");
        this.uri = Objects.requireNonNull(uri, "uri cannot be null");

        this.builder = HttpRequest.newBuilder(uri);

        this.httpContext = new JdkHttpContext(client.jdkHttpClient(), client.filterProcessor(), client.converterProcessor());
    }

    @Override
    public ReactiveRequestSpec header(String name, String... values) {
        Objects.requireNonNull(name, "name cannot be null");
        Arrays.stream(values).forEach(value -> this.builder.header(name, value));
        return this;
    }


    @Override
    public ReactiveRequestSpec accept(MediaType mediaType) {
        this._cachedAcceptMediaType = mediaType;
        this.builder.header(HttpHeaders.HeaderNames.ACCEPT, mediaType.toString());
        return this;
    }

    public MediaType accept() {
        return this._cachedAcceptMediaType;
    }

    @Override
    public <TRequestBody> ReactiveRequestSpec body(TRequestBody data) {
        var bytes = this.client.converterProcessor().write(new JdkHttpClientSerializationContext(data, this::accept));
        this.requestBodyHandler = HttpRequest.BodyPublishers.ofByteArray(bytes);
        return this;
    }

    @Override
    public ReactiveRequestSpec timeout(Duration timeout) {
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
    public ReactiveResponseSpec<Void> response() {
        final var discardingBodyHandler = new DiscardingJdkResponseBodyHandler(this.httpContext);
        return new JdkHttpClientResponseSpec<>(this.client, this, discardingBodyHandler, this.httpContext);
    }

    HttpRequest buildJdkHttpRequest() {
        return this.builder.method(this.method, this.requestBodyHandler).build();
    }
}
