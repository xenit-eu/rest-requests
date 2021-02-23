package eu.xenit.restrequests.api.exceptions;

public class HttpTimeoutException extends RuntimeException {

    public HttpTimeoutException(Throwable cause) {
        super(cause);
    }
}
