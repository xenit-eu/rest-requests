package eu.xenit.restrequests.client.jdk11;

import eu.xenit.restrequests.api.http.HttpContext;
import eu.xenit.restrequests.api.http.HttpResponseInfo;
import eu.xenit.restrequests.impl.ConverterProcessor;
import eu.xenit.restrequests.impl.FilterProcessor;
import java.net.http.HttpClient;
import java.net.http.HttpResponse.ResponseInfo;

public class JdkHttpContext implements HttpContext {

    private final HttpClient jdkHttpClient;
    private final FilterProcessor filters;
    private final ConverterProcessor converters;

    JdkHttpContext(HttpClient jdkHttpClient, FilterProcessor filters, ConverterProcessor converters) {

        this.jdkHttpClient = jdkHttpClient;
        this.filters = filters;
        this.converters = converters;
    }

    private HttpResponseInfo responseInfo;

    public HttpResponseInfo getResponseInfo() {
        return this.responseInfo;
    }

    public void setResponseInfo(ResponseInfo responseInfo) {
        this.responseInfo = new JdkHttpResponseInfo(responseInfo);
    }

    HttpClient jdkHttpClient() {
        return this.jdkHttpClient;
    }

    FilterProcessor filterProcessor() {
        return this.filters;
    }

    ConverterProcessor converterProcessor() {
        return this.converters;
    }


}
