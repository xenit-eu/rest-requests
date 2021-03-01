package eu.xenit.restrequests.client.springwebclient;

import eu.xenit.restrequests.api.http.HttpHeaders;
import java.util.List;

public class SpringWebHeaders implements HttpHeaders {

    private final org.springframework.http.HttpHeaders headers;

    public SpringWebHeaders(org.springframework.http.HttpHeaders headers) {
        this.headers = headers;
    }

    @Override
    public List<String> get(String headerName) {
        return this.headers.get(headerName);
    }
}
