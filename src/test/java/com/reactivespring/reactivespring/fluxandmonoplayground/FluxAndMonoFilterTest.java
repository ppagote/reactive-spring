package com.reactivespring.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

class FluxAndMonoFilterTest {

    List<String> stringList = Arrays.asList("Adam", "Ana", "Jack", "Jenny");

    @Test
    void filterTest() {
        Flux<String> stringFlux = Flux
                .fromIterable(stringList)
                .filter(name -> name.startsWith("A"))
                .log();
        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("Adam", "Ana")
                .verifyComplete();
    }

    @Test
    void filterTestLength() {
        Flux<String> stringFlux = Flux
                .fromIterable(stringList)
                .filter(name -> name.length() > 4)
                .log();
        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("Jenny")
                .verifyComplete();
    }
}
