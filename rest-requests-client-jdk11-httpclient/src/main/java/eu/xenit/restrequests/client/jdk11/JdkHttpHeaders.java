package eu.xenit.restrequests.client.jdk11;

import eu.xenit.restrequests.api.http.HttpHeaders;
import java.util.List;
import java.util.Objects;

class JdkHttpHeaders implements HttpHeaders {

    private final java.net.http.HttpHeaders headers;

    JdkHttpHeaders(java.net.http.HttpHeaders headers) {
        Objects.requireNonNull(headers, "headers cannot be null");
        this.headers = headers;
    }

    @Override
    public List<String> get(String headerName) {
        Objects.requireNonNull(headerName, "headerName cannot be null");
        return this.headers.allValues(headerName);
    }
}
