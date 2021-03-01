package eu.xenit.restrequests.client.springwebclient;

import eu.xenit.restrequests.api.http.MediaType;

final class MediaTypeMapper {

    private MediaTypeMapper() {
    }

    static org.springframework.http.MediaType toSpring(MediaType mediaType) {
        return new org.springframework.http.MediaType(
                mediaType.getType(),
                mediaType.getSubtype(),
                mediaType.getParameters());
    }
}
