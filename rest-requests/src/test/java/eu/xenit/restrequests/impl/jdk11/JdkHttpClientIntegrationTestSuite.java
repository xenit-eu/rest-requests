package eu.xenit.restrequests.impl.jdk11;

import eu.xenit.restrequests.api.ReactiveRestBuilder;
import eu.xenit.restrequests.test.HttpIntegrationTestSuite;

class JdkHttpClientIntegrationTestSuite extends HttpIntegrationTestSuite {

    @Override
    public ReactiveRestBuilder<?> builder() {
        return new JdkHttpClientBuilder();
    }
}