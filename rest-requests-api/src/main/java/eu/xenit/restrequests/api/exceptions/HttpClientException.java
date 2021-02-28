package eu.xenit.restrequests.api.exceptions;

public class HttpClientException extends RuntimeException {

    public HttpClientException() {

    }

    public HttpClientException(Throwable throwable) {
        super(throwable);
    }

    public HttpClientException(String message) {
        super(message);
    }
}
