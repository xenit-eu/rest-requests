package eu.xenit.restrequests.impl.converter;

import eu.xenit.restrequests.api.converter.ConverterException;
import eu.xenit.restrequests.api.converter.HttpBodyConverter;
import eu.xenit.restrequests.api.http.MediaType;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class StringConverter implements HttpBodyConverter {

    private final Charset defaultCharset;
    private final List<MediaType> supportedMediaTypes;

    /**
     * Default constructor that uses {@code "ISO-8859-1"} as default character set.
     *
     * This is specified as the default for text/* by RFC2616
     *
     * @see <a href="https://tools.ietf.org/html/rfc2616#section-3.7.1>Section 3.7.1 of [RFC2616]</a>
     */
    public StringConverter() {
        this(StandardCharsets.ISO_8859_1);
    }

    /**
     * A constructor accepting a default charset to use if the requested content type does not specify one.
     */
    public StringConverter(Charset defaultCharset) {
        this.defaultCharset = Objects.requireNonNull(defaultCharset, "defaultCharset cannot be null");
        this.supportedMediaTypes = List.of(MediaType.TEXT_PLAIN, new MediaType("text", "*"));
    }


    @Override
    public <T> boolean canRead(DeserializationContext context, Class<T> type) {
        return type == String.class && this.supportsMediaType(context.getContentType());
    }

    @Override
    public <T> T read(DeserializationContext context, Class<T> type) throws ConverterException {
        var charset = context.getContentType() == null
                ? this.defaultCharset
                : context.getContentType().getCharset().orElse(this.defaultCharset);

        return type.cast(new String(context.getSource(), charset));

    }

    @Override
    public boolean canWrite(SerializationContext context) {
        return context.getSource().getClass() == String.class && this.supportsMediaType(context.getContentType());
    }

    @Override
    public byte[] write(SerializationContext context) throws ConverterException {
        var source = context.getSource();
        if (source == null) {
            return new byte[0];
        }

        var charset = context.getContentType() == null
                ? this.defaultCharset
                : context.getContentType().getCharset().orElse(this.defaultCharset);

        return source.toString().getBytes(charset);
    }

    @Override
    public Collection<MediaType> getSupportedMediaTypes() {
        return this.supportedMediaTypes;
    }
}
