package com.reactivespring.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FluxAndMonoTest {

    @Test
    void fluxTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();
        stringFlux
                .subscribe(System.out::println,
                        System.err::println,
                        () -> System.out.println("Completed"));
    }

    @Test
    void fluxTestElements_WithoutError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();
        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .verifyComplete();
    }

    @Test
    void fluxTestElements_WithErrorMessage() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .expectErrorMessage("Exception occurred")
                .verify();
    }

    @Test
    void fluxTestElements_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void fluxTestElementsCount_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNextCount(3)
                .expectErrorMessage("Exception occurred")
                .verify();
    }

    @Test
    void fluxTestElements_WithError1() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                //.expectError(RuntimeException.class)
                .expectErrorMessage("Exception occurred")
                .verify();
    }

    @Test
    void monoTest() {
        Mono<String> stringMono = Mono.just("Spring");
        StepVerifier
                .create(stringMono.log())
                .expectSubscription()
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    void monoTest_WithError() {
        Mono<String> stringMono = Mono.error(new RuntimeException("Exception occurred"));
        StepVerifier
                .create(stringMono.log())
                .expectSubscription()
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void monoTest_WithErrorMessage() {
        Mono<String> stringMono = Mono.error(new RuntimeException("Exception occurred"));
        StepVerifier
                .create(stringMono.log())
                .expectSubscription()
                .expectErrorMessage("Exception occurred")
                .verify();
    }
}
