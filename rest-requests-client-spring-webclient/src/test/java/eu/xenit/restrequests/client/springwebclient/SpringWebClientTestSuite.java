package eu.xenit.restrequests.client.springwebclient;

import eu.xenit.restrequests.api.reactive.ReactiveRestBuilder;
import eu.xenit.restrequests.test.HttpIntegrationTestSuite;

class SpringWebClientTestSuite extends HttpIntegrationTestSuite {

    @Override
    public ReactiveRestBuilder<?> builder() {
        return new SpringWebClientBuilder();
    }
}