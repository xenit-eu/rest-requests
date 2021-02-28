package eu.xenit.restrequests.test;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public interface EnableBlockHound {

    @Test
    default void verifyBlockHoundInstalled() {
        var mono = Mono.delay(Duration.ofMillis(1))
                .doOnNext(it -> {
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });


        StepVerifier.create(mono)
                .expectError(BlockingOperationError.class)
                .verify();
    }

}
