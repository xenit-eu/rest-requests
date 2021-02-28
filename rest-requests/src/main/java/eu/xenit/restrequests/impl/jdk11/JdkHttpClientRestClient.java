package eu.xenit.restrequests.impl.jdk11;

import eu.xenit.restrequests.api.reactive.ReactiveRequestSpec;
import eu.xenit.restrequests.api.reactive.ReactiveRestClient;
import eu.xenit.restrequests.api.converter.HttpBodyConverter;
import eu.xenit.restrequests.api.filter.RestClientFilter;
import eu.xenit.restrequests.impl.ConverterProcessor;
import eu.xenit.restrequests.impl.FilterProcessor;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.Collection;

class JdkHttpClientRestClient implements ReactiveRestClient {

    private final HttpClient client;
    private final ConverterProcessor converterProcessor;
    private final FilterProcessor filterProcessor;

    JdkHttpClientRestClient(HttpClient httpClient,
            Collection<HttpBodyConverter> httpBodyConverters,
            Collection<RestClientFilter> filters) {
        this.client = httpClient;
        this.converterProcessor = new ConverterProcessor(httpBodyConverters);
        this.filterProcessor = new FilterProcessor(filters);
    }

    public static JdkHttpClientRestClient newClient() {
        return builder().build();
    }

    public static JdkHttpClientBuilder builder() {
        return new JdkHttpClientBuilder();
    }

    @Override
    public ReactiveRequestSpec get(URI uri) {
        return new JdkHttpClientRequestSpec(this, "GET", uri);
    }

    @Override
    public ReactiveRequestSpec put(URI uri) {
        return new JdkHttpClientRequestSpec(this, "PUT", uri);
    }

    @Override
    public ReactiveRequestSpec post(URI uri) {
        return new JdkHttpClientRequestSpec(this, "POST", uri);
    }

    @Override
    public ReactiveRequestSpec delete(URI uri) {
        return new JdkHttpClientRequestSpec(this, "DELETE", uri);
    }


    HttpClient jdkHttpClient() {
        return client;
    }

    ConverterProcessor converterProcessor() {
        return converterProcessor;
    }

    FilterProcessor filterProcessor() {
        return filterProcessor;
    }
}
