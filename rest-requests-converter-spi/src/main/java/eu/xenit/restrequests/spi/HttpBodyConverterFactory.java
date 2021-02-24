package eu.xenit.restrequests.spi;

import eu.xenit.restrequests.api.converter.HttpBodyConverter;

public interface HttpBodyConverterFactory {

    HttpBodyConverter create();
}
