package eu.xenit.restrequests.api.exceptions;

public class HttpTimeoutException extends HttpClientException {

    public HttpTimeoutException(Throwable cause) {
        super(cause);
    }
}
