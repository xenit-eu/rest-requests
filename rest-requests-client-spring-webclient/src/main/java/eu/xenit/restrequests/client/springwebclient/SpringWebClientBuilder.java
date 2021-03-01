package eu.xenit.restrequests.client.springwebclient;

import eu.xenit.restrequests.api.converter.HttpBodyConverter;
import eu.xenit.restrequests.api.filter.RestClientFilter;
import eu.xenit.restrequests.api.reactive.ReactiveRestClient;
import eu.xenit.restrequests.impl.BaseBuilder;
import java.time.Duration;
import java.util.Collection;
import org.springframework.web.reactive.function.client.WebClient;

public class SpringWebClientBuilder extends BaseBuilder<SpringWebClientBuilder> {

    @Override
    public SpringWebClientBuilder connectTimeout(Duration duration) {
        return null;
    }

    @Override
    public SpringWebClientBuilder followRedirects(boolean follow) {
        return null;
    }

    @Override
    public SpringWebClientBuilder converters(Collection<? extends HttpBodyConverter> converters) {
        return null;
    }

    @Override
    public SpringWebClientBuilder filters(Collection<? extends RestClientFilter> filters) {
        return null;
    }

    @Override
    public ReactiveRestClient build() {
        var webclient = WebClient.create();
        return new SpringWebClient(webclient);
    }
}
