package eu.xenit.restrequests.api.reactive;

import eu.xenit.restrequests.api.converter.HttpBodyConverter;
import eu.xenit.restrequests.api.filter.RestClientFilter;
import java.time.Duration;
import java.util.Collection;

public interface ReactiveRestBuilder<B extends ReactiveRestBuilder<B>> {

    B connectTimeout(Duration duration);
    B followRedirects(boolean follow);

    /**
     * Set the {@link HttpBodyConverter}s that should be used with the {@link ReactiveRestClient}.
     *
     * Setting this value will replace any previously configured converters or replace the default
     * converters on the builder.
     *
     * @param converters the converters to set
     * @return builder
     */
    B converters(Collection<? extends HttpBodyConverter> converters);

    /**
     * Set the {@link HttpBodyConverter}s that should be used with the {@link ReactiveRestClient}
     * to the default set. Calling this method will replace any previously defined converters.
     *
     * @return builder
     */
    B defaultConverters();


    B filters(Collection<? extends RestClientFilter> filters);

    B defaultFilters();

    ReactiveRestClient build();
}
