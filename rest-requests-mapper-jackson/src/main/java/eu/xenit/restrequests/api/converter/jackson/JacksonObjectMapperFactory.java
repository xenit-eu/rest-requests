package eu.xenit.restrequests.api.converter.jackson;

import eu.xenit.restrequests.api.converter.HttpBodyConverter;
import eu.xenit.restrequests.spi.ObjectMapperFactory;

public class JacksonObjectMapperFactory implements ObjectMapperFactory {

    @Override
    public HttpBodyConverter create() {
        return new JacksonObjectMapper();
    }
}
