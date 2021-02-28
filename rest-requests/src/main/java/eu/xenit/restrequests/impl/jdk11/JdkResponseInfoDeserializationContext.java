package eu.xenit.restrequests.impl.jdk11;

import eu.xenit.restrequests.api.converter.HttpBodyConverter.DeserializationContext;
import eu.xenit.restrequests.api.http.HttpHeaders;
import eu.xenit.restrequests.api.http.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse.ResponseInfo;
import java.util.function.Supplier;

class JdkResponseInfoDeserializationContext implements DeserializationContext {

    private final ResponseInfo responseInfo;
    private final InputStream inputStream;

    JdkResponseInfoDeserializationContext(ResponseInfo responseInfo, byte[] body) {
        this.responseInfo = responseInfo;
        this.inputStream = new ByteArrayInputStream(body);
    }

    JdkResponseInfoDeserializationContext(ResponseInfo responseInfo, InputStream inputStream) {
        this.responseInfo = responseInfo;
        this.inputStream = inputStream;
    }

    @Override
    public MediaType getContentType() {
        return this.responseInfo.headers()
                .firstValue(HttpHeaders.HeaderNames.CONTENT_TYPE)
                .map(MediaType::parseMediaType)
                .orElse(null);
    }

    @Override
    @Deprecated
    public byte[] getSource() {
        try {
            return this.inputStream.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

}
