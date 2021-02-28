package eu.xenit.restrequests.impl.converter;

import eu.xenit.restrequests.api.converter.ConverterException;
import eu.xenit.restrequests.api.converter.HttpBodyConverter;
import eu.xenit.restrequests.api.http.MediaType;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ByteArrayConverter implements HttpBodyConverter {

    private static final List<MediaType> MEDIA_TYPES = List.of(MediaType.APPLICATION_OCTET_STREAM);

    @Override
    public <T> boolean canRead(DeserializationContext context, Class<T> type) {
        return isByteArray(type);
    }

    @Override
    public <T> T read(DeserializationContext context, Class<T> type) throws ConverterException {
        try {
            return type.cast(context.getInputStream().readAllBytes());
        } catch (IOException e) {
            throw new ConverterException(e);
        }
    }

    @Override
    public boolean canWrite(SerializationContext context) {
        return isByteArray(context.getSource().getClass());
    }

    @Override
    public byte[] write(SerializationContext context) throws ConverterException {
        return (byte[]) context.getSource();
    }

    @Override
    public Collection<MediaType> getSupportedMediaTypes() {
        return MEDIA_TYPES;
    }

    private static <T> boolean isByteArray(Class<T> type) {
        return type.isArray() && byte.class.isAssignableFrom(type.getComponentType());
    }
}
