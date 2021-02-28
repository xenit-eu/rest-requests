package eu.xenit.restrequests.api.converter.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import eu.xenit.restrequests.api.http.MediaType;
import eu.xenit.restrequests.api.converter.ConverterException;
import eu.xenit.restrequests.api.converter.HttpBodyConverter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class JacksonBodyConverter implements HttpBodyConverter {

    private final ObjectMapper objectMapper;
    private final List<MediaType> supportedMediaTypes;

    public JacksonBodyConverter() {
        this(JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build());
    }

    public JacksonBodyConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.supportedMediaTypes = List.of(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }

    @Override
    public <T> boolean canRead(DeserializationContext context, Class<T> type) {
        Objects.requireNonNull(context, "Argument 'context' cannot be null");
        Objects.requireNonNull(type, "Argument 'type' cannot be null");

        // check the media type
        if (!this.supportsMediaType(context.getContentType())) {
            return false;
        }

        var javaType = objectMapper.constructType(type);

        AtomicReference<Throwable> causeRef = new AtomicReference<>();
        if (this.objectMapper.canDeserialize(javaType, causeRef)) {
            return true;
        }

        // TODO we could log causeRef
        return false;

    }

    /**
     * Checks if this object mapper supports this media type.
     *
     * Used for both reading and writing.
     *
     * @param mediaType to convert from or into
     * @return {@code true} if mediaType is null or is compatible, otherwise {@code false}
     */
    public boolean supportsMediaType(MediaType mediaType) {
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


    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList(this.supportedMediaTypes);
    }

    @Override
    public <T> T read(DeserializationContext context, Class<T> type) throws ConverterException {
        Objects.requireNonNull(context, "Argument 'context' cannot be null");
        Objects.requireNonNull(type, "Argument 'type' cannot be null");

        try {
            return objectMapper.readValue(context.getInputStream(), type);
        } catch (IOException ex) {
            throw new ConverterException(ex);
        }
    }

    @Override
    public boolean canWrite(SerializationContext context) {
        Objects.requireNonNull(context, "Argument 'context' cannot be null");

        // check if *any* of the content-types match
        if (!this.supportsMediaType(context.getContentType())) {
            return false;
        }


        AtomicReference<Throwable> causeRef = new AtomicReference<>();
        if (this.objectMapper.canSerialize(context.getSource().getClass())) {
            return true;
        }
        // TODO we could log causeRef

        return false;
    }

    @Override
    public byte[] write(SerializationContext context) throws ConverterException {
        Objects.requireNonNull(context, "Argument 'context' cannot be null");

        try {
            return objectMapper.writeValueAsBytes(context.getSource());
        } catch (JsonProcessingException e) {
            throw new ConverterException(e);
        }

    }
}
