package eu.xenit.restrequests.impl;

import eu.xenit.restrequests.api.reactive.ReactiveRestClient;
import eu.xenit.restrequests.api.reactive.ReactiveRestBuilder;
import eu.xenit.restrequests.api.converter.HttpBodyConverter;
import eu.xenit.restrequests.api.filter.RestClientFilter;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

public abstract class BaseBuilder<B extends BaseBuilder<B>> implements ReactiveRestBuilder<B> {

    /**
     * Set the {@link HttpBodyConverter ObjectMappers} that should be used with the {@link ReactiveRestClient} to the default
     * set. Calling this method will replace any previously defined converters.
     *
     * @return builder
     */
    @Override
    public B defaultConverters() {
        var objectMappers = ServiceLoader.load(HttpBodyConverter.class).stream()
                .map(Provider::get)
                .sorted((mapper1, mapper2) -> 0)
                .collect(Collectors.toList());

        return this.converters(objectMappers);
    }

    /**
     * Set the {@link HttpBodyConverter ObjectMappers} that should be used with the {@link ReactiveRestClient} to the default
     * set. Calling this method will replace any previously defined converters.
     *
     * @return builder
     */
    @Override
    public B defaultFilters() {
        var objectMappers = ServiceLoader.load(RestClientFilter.class).stream()
                .map(Provider::get)
                .sorted((mapper1, mapper2) -> 0)
                .collect(Collectors.toList());

        return this.filters(objectMappers);
    }


}
