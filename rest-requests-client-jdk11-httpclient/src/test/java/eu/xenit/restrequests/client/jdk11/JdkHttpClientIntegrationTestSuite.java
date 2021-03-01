package eu.xenit.restrequests.client.jdk11;

import eu.xenit.restrequests.api.reactive.ReactiveRestBuilder;
import eu.xenit.restrequests.test.HttpIntegrationTestSuite;

class JdkHttpClientIntegrationTestSuite extends HttpIntegrationTestSuite {

    @Override
    public ReactiveRestBuilder<?> builder() {
        return new JdkHttpClientBuilder();
    }
}