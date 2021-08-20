package com.reactivespring.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class FluxAndMonoBackPressureTest {

    @Test
    void backPressureTest(){
        Flux<Integer> integerFlux = Flux
                .range(1, 10)
                .log();

        StepVerifier
                .create(integerFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    void backPressure(){
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        integerFlux.subscribe((element) ->System.out.println("Element is " + element),
                        (ex) -> System.err.println("Exception is " + ex),
                        () -> System.out.println("Done"),
                (subscription -> subscription.request(2)));

    }

    @Test
    void backPressure_cancel(){
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        integerFlux.subscribe((element) ->System.out.println("Element is " + element),
                (ex) -> System.err.println("Exception is " + ex),
                () -> System.out.println("Done"),
                (Subscription::cancel));

    }

    @Test
    void custiomized_backPressure(){
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        integerFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                super.hookOnNext(value);
                request(1);
                System.out.println("Value " + value);
                if (value == 4) {
                    cancel();
                }
            }
        });

    }
}
