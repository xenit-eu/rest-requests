package eu.xenit.restrequests.api.converter;

import eu.xenit.restrequests.api.http.MediaType;
import java.util.Collection;

/**
 * An object mapper is used to serialize and deserialize a Java object to and from a String, byte[] or InputStream.
 */
public interface HttpBodyConverter {



    <T> boolean canRead(DeserializationContext context, Class<T> type);
    <T> T read(DeserializationContext context, Class<T> type) throws ConverterException;

    boolean canWrite(SerializationContext context);
    byte[] write(SerializationContext context) throws ConverterException;

    Collection<MediaType> getSupportedMediaTypes();

    /**
     *
     * Checks if this {@link HttpBodyConverter} supports the provided media type.
     *
     * Used for both reading and writing.
     *
     * @param mediaType to convert from or into
     * @return {@code true} if mediaType is null or is compatible, otherwise {@code false}
     */
    default boolean supportsMediaType(MediaType mediaType) {
        if (mediaType == null) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.includes(mediaType)) {
                return true;
            }
        }
        return false;
    }


    interface DeserializationContext {

        /**
         * @return The content type of the response
         */
        MediaType getContentType();

        byte[] getSource();
    }

    interface SerializationContext {

        /**
         * @return The object to serialize
         */
        Object getSource();

        /**
         * @return The target content type of the request
         */
        MediaType getContentType();


    }
}
