package eu.xenit.restrequests.api.reactive;

import eu.xenit.restrequests.api.http.HttpResponse;
import eu.xenit.restrequests.api.http.MediaType;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface ReactiveRequestSpec {

    ReactiveRequestSpec header(String name, String... values);
    ReactiveRequestSpec accept(MediaType mediaType);

    default ReactiveRequestSpec accept(String mediaType) {
        return accept(MediaType.parseMediaType(mediaType));
    }

    <TRequestBody> ReactiveRequestSpec body(TRequestBody data);

    ReactiveRequestSpec timeout(Duration timeout);

    URI uri();
    String method();

    ReactiveResponseSpec<Void> response();

    default <TResponse> ReactiveResponseSpec<TResponse> response(Class<TResponse> type) {
        return this.response().body(type);
    }

    default CompletableFuture<HttpResponse<Void>> execute() {
        return this.response().execute();
    }

    default <TResponse> CompletableFuture<HttpResponse<TResponse>> execute(Class<TResponse> type) {
        return this.response().body(type).execute();
    }
}
