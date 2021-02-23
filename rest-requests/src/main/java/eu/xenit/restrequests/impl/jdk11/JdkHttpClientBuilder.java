package eu.xenit.restrequests.impl.jdk11;

import eu.xenit.restrequests.api.filters.ReactiveRestClientFilter;
import eu.xenit.restrequests.impl.BaseBuilder;
import eu.xenit.restrequests.api.converter.HttpBodyConverter;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

class JdkHttpClientBuilder extends BaseBuilder<JdkHttpClientBuilder> {

    private Duration connectTimeout = Duration.ofSeconds(10);

    private final Set<HttpBodyConverter> httpBodyConverters = new LinkedHashSet<>();
    private final Set<ReactiveRestClientFilter> filters = new LinkedHashSet<>();

    private boolean followRedirects = true;

    JdkHttpClientBuilder() {
        this.defaultConverters();
        this.defaultFilters();
    }

    public JdkHttpClientBuilder connectTimeout(Duration duration) {
        this.connectTimeout = Objects.requireNonNull(duration, "duration cannot be null");
        return this;
    }

    @Override
    public JdkHttpClientBuilder followRedirects(boolean follow) {
        this.followRedirects = follow;
        return this;
    }

    @Override
    public JdkHttpClientBuilder converters(Collection<? extends HttpBodyConverter> converters) {
        this.httpBodyConverters.clear();
        this.httpBodyConverters.addAll(converters);
        return this;
    }

    @Override
    public JdkHttpClientBuilder filters(Collection<? extends ReactiveRestClientFilter> filters) {
        this.filters.clear();
        this.filters.addAll(filters);
        return this;
    }

    public JdkHttpClientRestClient build() {
        var inner = HttpClient.newBuilder();

        inner.connectTimeout(connectTimeout);
        inner.followRedirects(followRedirects ? Redirect.NORMAL : Redirect.NEVER);

        return new JdkHttpClientRestClient(inner.build(), httpBodyConverters, filters);
    }
}
