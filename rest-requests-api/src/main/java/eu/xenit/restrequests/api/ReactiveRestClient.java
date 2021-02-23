package eu.xenit.restrequests.api;

import eu.xenit.restrequests.api.http.HttpHeaders;
import eu.xenit.restrequests.api.http.MediaType;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface ReactiveRestClient {

    RequestSpecification get(URI uri);
    RequestSpecification put(URI uri);
    RequestSpecification post(URI uri);
    RequestSpecification delete(URI uri);

    default RequestSpecification get(String uri) {
        return this.get(URI.create(uri));
    }
    default RequestSpecification put(String uri) {
        return this.put(URI.create(uri));
    }
    default RequestSpecification post(String uri) {
        return this.post(URI.create(uri));
    }
    default RequestSpecification delete(String uri) {
        return this.delete(URI.create(uri));
    }

    interface RequestSpecification {

        RequestSpecification header(String name, String... values);
        RequestSpecification accept(MediaType mediaType);

        <TRequestBody> RequestSpecification body(TRequestBody data);

        RequestSpecification timeout(Duration timeout);

        URI uri();
        String method();

        ResponseSpecification<Void> response();

        default <TResponse> ResponseSpecification<TResponse> response(Class<TResponse> type) {
            return this.response().body(type);
        }

        default <TResponse> CompletableFuture<RestResponse<TResponse>> execute(Class<TResponse> type) {
            return this.response().body(type).execute();
        }

        default RequestSpecification accept(String mediaType) {
            return accept(MediaType.parseMediaType(mediaType));
        }
    }


    interface ResponseSpecification<TResponse> {
        <NewTResponse> ResponseSpecification<NewTResponse> body(Class<NewTResponse> type);
        CompletableFuture<RestResponse<TResponse>> execute();
    }

    interface RestResponse<TResponse> {
        TResponse body();
        HttpHeaders headers();
        int statusCode();
    }
}
