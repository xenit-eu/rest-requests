package eu.xenit.restrequests.impl.filter;

import eu.xenit.restrequests.api.exceptions.HttpStatusException;
import eu.xenit.restrequests.api.filter.ResponseFilter;
import eu.xenit.restrequests.api.http.HttpContext;

public class HttpStatusExceptionFilter implements ResponseFilter {

    @Override
    public void filter(HttpContext context) {
        if (context.getResponseInfo().statusCode() >= 400) {
            throw HttpStatusException.from(context.getResponseInfo());
        }
    }
}
