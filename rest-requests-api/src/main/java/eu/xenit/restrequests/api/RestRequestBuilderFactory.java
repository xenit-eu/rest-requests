package eu.xenit.restrequests.api;

public interface RestRequestBuilderFactory {
    ReactiveRestBuilder<?> createBuilder();
}
