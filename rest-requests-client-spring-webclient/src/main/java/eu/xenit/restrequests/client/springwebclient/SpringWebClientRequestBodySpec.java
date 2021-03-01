package eu.xenit.restrequests.client.springwebclient;

import eu.xenit.restrequests.api.reactive.ReactiveRequestSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;

public class SpringWebClientRequestBodySpec extends SpringWebClientRequestSpec {

    private final RequestBodySpec requestBodySpec;

    public SpringWebClientRequestBodySpec(SpringWebClient springWebClient,
            RequestBodySpec requestBodySpec) {
        super(springWebClient, requestBodySpec);

        this.requestBodySpec = requestBodySpec;
    }

    @Override
    public <TRequestBody> ReactiveRequestSpec body(TRequestBody body) {
        requestBodySpec.bodyValue(body);
        return this;
    }
}
