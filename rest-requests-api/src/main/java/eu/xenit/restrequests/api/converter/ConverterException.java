package eu.xenit.restrequests.api.converter;

public class ConverterException extends RuntimeException {

    public ConverterException(String message) {
        super(message);
    }

    public ConverterException(Throwable cause) {
        super(cause);
    }

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }
}
