package eu.xenit.restrequests.api.converter.jackson;

import eu.xenit.restrequests.api.converter.HttpBodyConverter;
import eu.xenit.restrequests.spi.HttpBodyConverterFactory;

public class JacksonHttpBodyConverterFactory implements HttpBodyConverterFactory {

    @Override
    public HttpBodyConverter create() {
        return new JacksonBodyConverter();
    }
}
