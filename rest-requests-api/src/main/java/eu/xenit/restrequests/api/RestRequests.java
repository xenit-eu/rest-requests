package eu.xenit.restrequests.api;

import eu.xenit.restrequests.api.reactive.ReactiveRestBuilder;
import eu.xenit.restrequests.api.reactive.ReactiveRestRequestBuilderFactory;
import java.util.ServiceLoader;

public class RestRequests {

    public static ReactiveRestBuilder<?> reactive() {

        return ServiceLoader.load(ReactiveRestRequestBuilderFactory.class)
                .stream()
                .sorted()
                .findFirst().orElseThrow(() -> {
                    String message = String.format("No %s implementations found. "
                                    + "Add 'eu.xenit.restrequests:rest-requests' on the runtime classpath",
                            ReactiveRestRequestBuilderFactory.class);
                    throw new RuntimeException(message);
                })
                .get().createBuilder();
    }

}
