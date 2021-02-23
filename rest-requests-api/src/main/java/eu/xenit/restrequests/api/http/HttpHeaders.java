package eu.xenit.restrequests.api.http;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public interface HttpHeaders {

    final class HeaderNames {
        private HeaderNames() {
        }

        public static final String LOCATION = "Location";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String ACCEPT = "Accept";
    }

    /**
     * Get the list of header values for the given header name, if any.
     *
     * @param headerName the header name
     * @return the list of header values, or an empty list
     */
    List<String> get(String headerName);


    /**
     * Return the first header value for the given header name, if any.
     *
     * @param headerName the header name
     * @return the first header value, or {@code Optional.empty()} if none
     */
    default Optional<String> getFirst(String headerName) {
        return this.get(headerName).stream().findFirst();
    }

    /**
     * Return the first header value for the given header name, if any.
     *
     * @param headerName the header name
     * @return the first header value, or {@code null} if none
     */
    default Optional<String> getFirstOrEmpty(String headerName) {
        return this.get(headerName).stream().findFirst();
    }


    /**
     * Return the (new) location of a resource as specified by the {@code Location} header.
     * <p>Returns {@code Optional.empty()} when the location is unknown.
     */
    default Optional<URI> getLocation() {
        return this.getFirst(HeaderNames.LOCATION).map(URI::create);
    }

    /**
     * Return the content-type of the body, as specified by the {@code Content-Type} header.
     *
     * @return The {@link MediaType} if present, otherwise {@code Optional.empty()}
     */
    default Optional<MediaType> getContentType() {
        return getFirst(HeaderNames.CONTENT_TYPE).map(MediaType::parseMediaType);
    }

    default Charset getContentTypeCharsetOrDefault() {
        return this.getContentType().map(contentType -> contentType.getCharset()
                .orElseGet(() -> {
                    // RFC-2616 Section 3.7.1
                    //   When no explicit charset  parameter is provided by the sender,
                    //   media subtypes of the "text" type are defined to have a default
                    //   charset value of "ISO-8859-1" when received via HTTP.
                    if (MediaType.TEXT_ALL.includes(contentType)) {
                        return StandardCharsets.ISO_8859_1;
                    }

                    // Not sure charsets make sense outside the scope of 'text/*' ?
                    return Charset.defaultCharset();
                }))
                .orElse(Charset.defaultCharset());

    }
}
