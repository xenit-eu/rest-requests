package eu.xenit.restrequests.client.jdk11;

import eu.xenit.restrequests.api.http.HttpHeaders;
import eu.xenit.restrequests.api.http.HttpResponse;

class JdkHttpClientResponse<TResponse> implements HttpResponse<TResponse> {


    private final java.net.http.HttpResponse<TResponse> response;

    JdkHttpClientResponse(java.net.http.HttpResponse<TResponse> httpResponse) {
        this.response = httpResponse;
    }

    @Override
    public TResponse body() {
        return response.body();
    }

    public HttpHeaders headers() {
        return new JdkHttpHeaders(response.headers());
    }

    @Override
    public int statusCode() {
        return this.response.statusCode();
    }

}
