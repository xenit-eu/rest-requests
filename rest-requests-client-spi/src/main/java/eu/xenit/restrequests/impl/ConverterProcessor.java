package eu.xenit.restrequests.impl;

import eu.xenit.restrequests.api.converter.ConverterException;
import eu.xenit.restrequests.api.converter.HttpBodyConverter;
import eu.xenit.restrequests.api.converter.HttpBodyConverter.DeserializationContext;
import eu.xenit.restrequests.api.converter.HttpBodyConverter.SerializationContext;
import eu.xenit.restrequests.api.converter.SerializationException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

public class ConverterProcessor {

    private final Collection<? extends HttpBodyConverter> objectMappers;

    public ConverterProcessor(Collection<? extends HttpBodyConverter> objectMappers) {
        this.objectMappers = Collections.unmodifiableSet(new LinkedHashSet<>(objectMappers));
    }

    public <T> T read(DeserializationContext context, Class<T> type) {

        return this.objectMappers.stream()

                // figure out which object-mapper CAN read the Content-Type
                .filter(mapper -> mapper.canRead(context, type))
                .findFirst()

                // if none found, bail out with an exception
                .orElseThrow(() -> {
                    String msg = String.format("Cannot convert %s into %s", context.getContentType().toString(), type.getName());
                    return new ConverterException(msg);
                })

                // if found, use it to deserialize the source into the target type
                .read(context, type);
    }

    public byte[] write(SerializationContext context) {
        return this.objectMappers.stream()

                // figure out which object-mapper CAN write to this content type
                .filter(mapper -> mapper.canWrite(context))
                .findFirst()

                // if none found, bail out with an exception
                .orElseThrow(() -> {
                    String msg = String.format("Cannot convert %s into %s",
                            context.getSource().getClass().getName(), context.getContentType());
                    return new ConverterException(msg);
                })

                // if found, use it to deserialize the source into the target type
                .write(context);
    }

}
