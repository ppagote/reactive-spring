package com.reactivespring.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;


class VirtualTimeTest {

    @Test
    void testingWithoutVirtualTime() {
        Flux<Long> integerFlux = Flux.interval(Duration.ofSeconds(1)).take(3);

        StepVerifier
                .create(integerFlux.log())
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    void testingWithVirtualTime() {
        VirtualTimeScheduler.getOrSet();
        Flux<Long> integerFlux = Flux.interval(Duration.ofSeconds(1)).take(3);

        StepVerifier
                .withVirtualTime(integerFlux::log)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }
}
