package eu.xenit.restrequests.impl.jdk11;

import eu.xenit.restrequests.api.ReactiveRestBuilder;
import eu.xenit.restrequests.api.RestRequestBuilderFactory;

public class JdkHttpClientBuilderFactory implements RestRequestBuilderFactory {

    @Override
    public ReactiveRestBuilder<?> createBuilder() {
        return new JdkHttpClientBuilder();
    }
}
