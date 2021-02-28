package eu.xenit.restrequests.api.reactive;

import java.net.URI;

public interface ReactiveRestClient {

    ReactiveRequestSpec get(URI uri);
    ReactiveRequestSpec put(URI uri);
    ReactiveRequestSpec post(URI uri);
    ReactiveRequestSpec delete(URI uri);

    default ReactiveRequestSpec get(String uri) {
        return this.get(URI.create(uri));
    }
    default ReactiveRequestSpec put(String uri) {
        return this.put(URI.create(uri));
    }
    default ReactiveRequestSpec post(String uri) {
        return this.post(URI.create(uri));
    }
    default ReactiveRequestSpec delete(String uri) {
        return this.delete(URI.create(uri));
    }
}
