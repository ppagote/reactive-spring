package com.reactivespring.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

class FLuxAndMonoErrorTest {

    @Test
    void fluxErrorHandling(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .concatWith(Flux.just("E"))
                .onErrorResume((e) -> {
                    System.out.println(e);
                    return Flux.just("default", "default1");
                });

        StepVerifier
                .create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "default", "default1")
                .verifyComplete();
    }

    @Test
    void fluxErrorHandling_onErrorReturn(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .concatWith(Flux.just("E"))
                .onErrorReturn("default");

        StepVerifier
                .create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "default")
                .verifyComplete();
    }

    @Test
    void fluxErrorHandling_onErrorMap(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .concatWith(Flux.just("E"))
                .onErrorMap(CustomException::new);

        StepVerifier
                .create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    void fluxErrorHandling_onErrorMap_withRetry(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .concatWith(Flux.just("E"))
                .onErrorMap(CustomException::new)
                .retry(1);

        StepVerifier
                .create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    void fluxErrorHandling_onErrorMap_withRetryBackOff(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .concatWith(Flux.just("E"))
                .onErrorMap(CustomException::new)
                .retryWhen(Retry.backoff(1, Duration.ofSeconds(5)));

        StepVerifier
                .create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(IllegalStateException.class)
                .verify();
    }
}
