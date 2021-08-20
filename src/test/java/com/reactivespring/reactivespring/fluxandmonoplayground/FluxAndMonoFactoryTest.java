package com.reactivespring.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

class FluxAndMonoFactoryTest {
    List<String> stringList = Arrays.asList("Adam", "Ana", "Jack", "Jenny");

    @Test
    void flexUsingIterable() {
        Flux<String> stringFlux = Flux.fromIterable(stringList);

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("Adam", "Ana", "Jack", "Jenny")
                .verifyComplete();
    }

    @Test
    void flexUsingArray() {
        String[] strings = new String[]{"Adam", "Ana", "Jack", "Jenny"};
        Flux<String> stringFlux = Flux.fromArray(strings);

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("Adam", "Ana", "Jack", "Jenny")
                .verifyComplete();
    }

    @Test
    void fluxUsingStream() {
        Stream<String> stream = stringList.stream();
        Flux<String> stringFlux = Flux.fromStream(stream);

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("Adam", "Ana", "Jack", "Jenny")
                .verifyComplete();
    }

    @Test
    void monoUsingJustOrEmpty() {
        Mono<Object> objectMono = Mono.justOrEmpty(null);

        StepVerifier
                .create(objectMono.log())
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void monoUsingSuppliers() {
        Supplier<String> stringSupplier = () -> "adam";
        Mono<Object> objectMono = Mono.fromSupplier(stringSupplier);

        StepVerifier
                .create(objectMono.log())
                .expectSubscription()
                .expectNext("adam")
                .verifyComplete();
    }

    @Test
    void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(1, 5);

        StepVerifier
                .create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }
}
