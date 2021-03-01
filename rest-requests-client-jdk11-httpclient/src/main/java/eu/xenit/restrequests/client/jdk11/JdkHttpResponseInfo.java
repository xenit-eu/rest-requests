package eu.xenit.restrequests.client.jdk11;

import eu.xenit.restrequests.api.http.HttpHeaders;
import eu.xenit.restrequests.api.http.HttpResponseInfo;
import java.net.http.HttpResponse.ResponseInfo;

public class JdkHttpResponseInfo implements HttpResponseInfo {

    private final ResponseInfo responseInfo;

    public JdkHttpResponseInfo(ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
    }

    @Override
    public int statusCode() {
        return this.responseInfo.statusCode();
    }

    @Override
    public HttpHeaders headers() {
        return new JdkHttpHeaders(this.responseInfo.headers());
    }
}
