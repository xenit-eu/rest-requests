package eu.xenit.restrequests.impl;

import eu.xenit.restrequests.api.filter.ResponseBodyStreamFilter;
import eu.xenit.restrequests.api.filter.ResponseFilter;
import eu.xenit.restrequests.api.filter.RestClientFilter;
import eu.xenit.restrequests.api.http.HttpContext;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public class FilterProcessor {


    /**
     * List of registered filters.
     */
    private final Collection<? extends RestClientFilter> filters;

    /**
     * Constructor that accepts a list of filters.
     *
     * @param filters Filters to register with the filter processor.
     */
    public FilterProcessor(Collection<? extends RestClientFilter> filters) {
        this.filters = filters;
    }

    /**
     * Returns a stream of all registered {@link RestClientFilter} instances with the given type.
     *
     * @return A stream of all registered {@link RestClientFilter} instances.
     */
    private <T extends RestClientFilter> Stream<T> getFiltersWithType(Class<T> filterType) {
        return filters.stream().filter(filterType::isInstance).map(filterType::cast);

    }

    /**
     * Filters the response {@link InputStream} of the request.
     *
     * @param inputStream {@link InputStream} of the request.
     * @return Filtered {@link InputStream}.
     */
    public InputStream filterBodyResponseStream(InputStream inputStream) {
        return this.getFiltersWithType(ResponseBodyStreamFilter.class)
                .reduce(inputStream, (upstream, filter) -> filter.filter(upstream), PARALLEL_STREAMS_NOT_SUPPORTED);
    }

    public void onResponse(HttpContext context) {
        this.getFiltersWithType(ResponseFilter.class).forEach(filter -> {
            filter.filter(context);
        });
    }

    private final BinaryOperator<InputStream> PARALLEL_STREAMS_NOT_SUPPORTED = (l1, l2) -> {
        throw new UnsupportedOperationException("parallel streams not supported");
    };


}
