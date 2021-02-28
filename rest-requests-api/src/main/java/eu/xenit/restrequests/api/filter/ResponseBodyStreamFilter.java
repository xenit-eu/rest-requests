package eu.xenit.restrequests.api.filter;

import eu.xenit.restrequests.api.converter.HttpBodyConverter;
import java.io.InputStream;

/**
 * A filter that allows modification of the response body {@link InputStream}
 * before it is processed by the {@link HttpBodyConverter}.
 */
public interface ResponseBodyStreamFilter extends RestClientFilter {
    /**
     * Filters the response body {@link InputStream}
     *
     * @param inputStream The {@link InputStream} of the request.
     * @return Filtered request {@link InputStream}.
     */
    InputStream filter(InputStream inputStream);
}