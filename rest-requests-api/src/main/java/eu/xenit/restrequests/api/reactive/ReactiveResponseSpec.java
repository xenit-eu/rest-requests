package eu.xenit.restrequests.api.reactive;

import eu.xenit.restrequests.api.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public interface ReactiveResponseSpec<TResponse> {
    <NewTResponse> ReactiveResponseSpec<NewTResponse> body(Class<NewTResponse> type);
    CompletableFuture<HttpResponse<TResponse>> execute();
}
