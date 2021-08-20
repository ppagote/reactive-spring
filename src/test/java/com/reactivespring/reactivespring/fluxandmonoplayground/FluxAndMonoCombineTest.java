package com.reactivespring.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

class FluxAndMonoCombineTest {

    @Test
    void combineUsingMerge() {
        Flux<String> stringFlux1 = Flux.just("A", "B", "C");
        Flux<String> stringFlux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.merge(stringFlux1, stringFlux2);

        StepVerifier
                .create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void combineUsingMerge_withDelay() {
        Flux<String> stringFlux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> stringFlux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        //merge do not maintain order
        Flux<String> mergedFlux = Flux.merge(stringFlux1, stringFlux2);

        StepVerifier
                .create(mergedFlux.log())
                .expectSubscription()
                .expectNextCount(6)
                //.expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void combineUsingConcat() {
        Flux<String> stringFlux1 = Flux.just("A", "B", "C");
        Flux<String> stringFlux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.concat(stringFlux1, stringFlux2);

        StepVerifier
                .create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void combineUsingConcat_withDelay() {
        VirtualTimeScheduler.getOrSet();
        Flux<String> stringFlux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> stringFlux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.concat(stringFlux1, stringFlux2);

       /* StepVerifier
                .create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();*/
        StepVerifier
                .withVirtualTime(mergedFlux:: log)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void combineUsingZip() {
        Flux<String> stringFlux1 = Flux.just("A", "B", "C");
        Flux<String> stringFlux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.zip(stringFlux1, stringFlux2, String::concat);

        StepVerifier
                .create(mergedFlux.log())
                .expectSubscription()
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }
}
