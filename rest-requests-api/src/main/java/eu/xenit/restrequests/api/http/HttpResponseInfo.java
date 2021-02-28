package eu.xenit.restrequests.api.http;

/**
 * HTTP response information without body payload.
 */
public interface HttpResponseInfo {
    HttpHeaders headers();
    int statusCode();
}
