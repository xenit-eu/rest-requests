package eu.xenit.restrequests.client.jdk11;

import eu.xenit.restrequests.api.converter.HttpBodyConverter.SerializationContext;
import eu.xenit.restrequests.api.http.MediaType;
import java.util.function.Supplier;

class JdkHttpClientSerializationContext implements SerializationContext {
    private final Object source;
    private final Supplier<MediaType> mediaTypeSupplier;

    JdkHttpClientSerializationContext(Object source, Supplier<MediaType> mediaTypeSupplier) {
        this.source = source;
        this.mediaTypeSupplier = mediaTypeSupplier;
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    @Override
    public MediaType getContentType() {
        return this.mediaTypeSupplier.get();
    }
}
