package eu.xenit.restrequests.client.springwebclient;

import eu.xenit.restrequests.api.reactive.ReactiveRequestSpec;
import eu.xenit.restrequests.api.reactive.ReactiveRestClient;
import java.net.URI;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;

public class SpringWebClient implements ReactiveRestClient {

    private final WebClient webClient;

    SpringWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public ReactiveRequestSpec get(URI uri) {
        final RequestHeadersSpec<?> webclientRequestSpec = webClient.get().uri(uri);
        return new SpringWebClientRequestSpec(this, webclientRequestSpec);
    }

    @Override
    public ReactiveRequestSpec put(URI uri) {
        final RequestBodySpec webclientRequestSpec = webClient.put().uri(uri);
        return new SpringWebClientRequestBodySpec(this, webclientRequestSpec);
    }

    @Override
    public ReactiveRequestSpec post(URI uri) {
        final RequestBodySpec webclientRequestSpec = webClient.post().uri(uri);
        return new SpringWebClientRequestBodySpec(this, webclientRequestSpec);
    }

    @Override
    public ReactiveRequestSpec delete(URI uri) {
        return null;
    }
}
