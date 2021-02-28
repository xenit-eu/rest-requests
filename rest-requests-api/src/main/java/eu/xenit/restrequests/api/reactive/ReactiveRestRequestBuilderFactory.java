package eu.xenit.restrequests.api.reactive;

import eu.xenit.restrequests.api.reactive.ReactiveRestBuilder;

public interface ReactiveRestRequestBuilderFactory {
    ReactiveRestBuilder<?> createBuilder();
}
