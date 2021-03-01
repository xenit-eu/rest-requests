package eu.xenit.restrequests.client.springwebclient;

import eu.xenit.restrequests.api.http.MediaType;
import eu.xenit.restrequests.api.reactive.ReactiveRequestSpec;
import eu.xenit.restrequests.api.reactive.ReactiveResponseSpec;
import java.net.URI;
import java.time.Duration;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

public class SpringWebClientRequestSpec implements ReactiveRequestSpec {

    private final SpringWebClient springWebClient;
    private final RequestHeadersSpec<?> webClientRequestSpec;

    public SpringWebClientRequestSpec(SpringWebClient springWebClient, RequestHeadersSpec<?> webClientRequestSpec) {

        this.springWebClient = springWebClient;
        this.webClientRequestSpec = webClientRequestSpec;
    }

    @Override
    public ReactiveRequestSpec header(String name, String... values) {
        this.webClientRequestSpec.header(name, values);
        return this;
    }

    @Override
    public ReactiveRequestSpec accept(MediaType mediaType) {
        this.webClientRequestSpec.accept(MediaTypeMapper.toSpring(mediaType));
        return this;
    }

    @Override
    public <TRequestBody> ReactiveRequestSpec body(TRequestBody data) {
        throw new UnsupportedOperationException("request spec does not support body payload");
    }

    @Override
    public ReactiveRequestSpec timeout(Duration timeout) {
        return null;
    }

    @Override
    public URI uri() {
        return null;
    }

    @Override
    public String method() {
        return null;
    }

    @Override
    public ReactiveResponseSpec<Void> response() {
        ResponseSpec responseSpec = this.webClientRequestSpec.retrieve();
        return new SpringWebClientReactiveRequestSpec<>(responseSpec, Void.class);
    }
}
