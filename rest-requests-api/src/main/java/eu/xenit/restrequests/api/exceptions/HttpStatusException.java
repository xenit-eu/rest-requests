package eu.xenit.restrequests.api.exceptions;

import eu.xenit.restrequests.api.http.HttpResponseInfo;

public class HttpStatusException extends HttpClientException {

    private final HttpResponseInfo response;

    private HttpStatusException(HttpResponseInfo responseInfo) {
        super("HTTP "+responseInfo.statusCode());
        this.response = responseInfo;
    }

    public static HttpStatusException from(HttpResponseInfo responseInfo) {
        return new HttpStatusException(responseInfo);
    }

    public int statusCode() {
        return this.response.statusCode();
    }
}
