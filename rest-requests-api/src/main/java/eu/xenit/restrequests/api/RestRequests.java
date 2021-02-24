package eu.xenit.restrequests.api;

import java.util.ServiceLoader;

public class RestRequests {

    public static ReactiveRestBuilder<?> reactive() {

        return ServiceLoader.load(RestRequestBuilderFactory.class)
                .stream()
                .sorted()
                .findFirst().orElseThrow(() -> {
                    String message = String.format("No %s implementations found. "
                                    + "Add 'eu.xenit.restrequests:rest-requests' on the runtime classpath",
                            RestRequestBuilderFactory.class);
                    throw new RuntimeException(message);
                })
                .get().createBuilder();
    }

}
