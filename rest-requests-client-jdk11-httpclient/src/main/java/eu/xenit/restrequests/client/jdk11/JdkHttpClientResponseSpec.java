package eu.xenit.restrequests.client.jdk11;

import eu.xenit.restrequests.api.exceptions.HttpClientException;
import eu.xenit.restrequests.api.exceptions.HttpTimeoutException;
import eu.xenit.restrequests.api.http.HttpResponse;
import eu.xenit.restrequests.api.reactive.ReactiveResponseSpec;
import java.net.http.HttpResponse.BodyHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

class JdkHttpClientResponseSpec<TResponse>
        implements ReactiveResponseSpec<TResponse> {

    private final JdkHttpClientRestClient client;
    private final JdkHttpClientRequestSpec request;
    private final BodyHandler<TResponse> responseBodyHandler;

    private final JdkHttpContext httpContext;

    JdkHttpClientResponseSpec(JdkHttpClientRestClient client, JdkHttpClientRequestSpec request,
            BodyHandler<TResponse> responseBodyHandler,
            JdkHttpContext httpContext) {
        this.client = client;
        this.request = request;
        this.responseBodyHandler = responseBodyHandler;

        this.httpContext = httpContext;
    }



    @Override
    public <NewResponse> ReactiveResponseSpec<NewResponse> body(Class<NewResponse> type) {
        final JdkResponseBodyHandler<NewResponse> bodyHandler = new JdkResponseBodyHandler<>(this.httpContext, type);
        return new JdkHttpClientResponseSpec<>(this.client, this.request, bodyHandler, this.httpContext);
    }

    @Override
    public CompletableFuture<? extends HttpResponse<TResponse>> execute() {


        var jdkRequest = request.buildJdkHttpRequest();

        return this.client.jdkHttpClient().sendAsync(jdkRequest, responseBodyHandler)
                .exceptionally(throwable -> {
                    // unwrap the CompletionException
                    if (throwable instanceof CompletionException) {
                        throwable = throwable.getCause();
                    }

                    if (throwable instanceof java.net.http.HttpTimeoutException) {
                        throw new HttpTimeoutException(throwable);
                    }

                    if (throwable instanceof HttpClientException) {
                        throw (HttpClientException) throwable;
                    }

                    throw new HttpClientException(throwable);
                })

//                .whenComplete((response, exception) -> {
//                    if (exception != null) {
//                        if (exception.getCause() instanceof java.net.http.HttpTimeoutException) {
//                            throw new HttpTimeoutException(exception);
//                        }
//                    }
//                })
                .thenApply(JdkHttpClientResponse::new);

    }

}
