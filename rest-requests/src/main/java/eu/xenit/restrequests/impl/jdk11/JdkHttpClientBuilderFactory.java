package eu.xenit.restrequests.impl.jdk11;

import eu.xenit.restrequests.api.reactive.ReactiveRestBuilder;
import eu.xenit.restrequests.api.reactive.ReactiveRestRequestBuilderFactory;

public class JdkHttpClientBuilderFactory implements ReactiveRestRequestBuilderFactory {

    @Override
    public ReactiveRestBuilder<?> createBuilder() {
        return new JdkHttpClientBuilder();
    }
}
