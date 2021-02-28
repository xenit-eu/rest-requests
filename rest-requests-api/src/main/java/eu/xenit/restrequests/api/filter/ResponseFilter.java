package eu.xenit.restrequests.api.filter;

import eu.xenit.restrequests.api.http.HttpContext;
import eu.xenit.restrequests.api.http.HttpResponseInfo;

/**
 * A {@link RestClientFilter} that allows modification ...
 */
public interface ResponseFilter extends RestClientFilter {

    /**
     * Provides an opportunity to modify the {@link HttpResponseInfo} before it is returned.
     *
     * @param context HTTP response.
     */
//    void filter(HttpResponseInfo response);
    void filter(HttpContext context);

}
