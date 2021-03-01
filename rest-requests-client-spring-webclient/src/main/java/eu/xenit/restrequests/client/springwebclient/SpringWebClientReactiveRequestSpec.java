package eu.xenit.restrequests.client.springwebclient;

import eu.xenit.restrequests.api.http.HttpHeaders;
import eu.xenit.restrequests.api.http.HttpResponse;
import eu.xenit.restrequests.api.reactive.ReactiveResponseSpec;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

public class SpringWebClientReactiveRequestSpec<T> implements ReactiveResponseSpec<T> {

    private final ResponseSpec webClientResponseSpec;
    private final Class<T> bodyType;

    SpringWebClientReactiveRequestSpec(ResponseSpec webClientResponseSpec, Class<T> bodyType) {

        this.webClientResponseSpec = webClientResponseSpec;
        this.bodyType = bodyType;
    }

    @Override
    public <TResponse> ReactiveResponseSpec<TResponse> body(Class<TResponse> type) {
        return new SpringWebClientReactiveRequestSpec<TResponse>(this.webClientResponseSpec, type);
    }

    @Override
    public CompletableFuture<? extends HttpResponse<T>> execute() {
        var entityMono = this.webClientResponseSpec.toEntity(bodyType);
        var responseMono = entityMono.map(entity -> new eu.xenit.restrequests.api.http.HttpResponse<T>() {
                    @Override
                    public HttpHeaders headers() {
                        return new SpringWebHeaders(entity.getHeaders());
                    }

                    @Override
                    public int statusCode() {
                        return entity.getStatusCodeValue();
                    }

                    @Override
                    public T body() {
                        return entity.getBody();
                    }
                });

        CompletableFuture<? extends HttpResponse<T>> future = responseMono.toFuture();
        return future;
    }
}
