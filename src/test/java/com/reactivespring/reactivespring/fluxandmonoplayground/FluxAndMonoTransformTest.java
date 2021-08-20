package com.reactivespring.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

class FluxAndMonoTransformTest {

    List<String> stringList = Arrays.asList("Adam", "Ana", "Jack", "Jenny");

    @Test
    void transformUsingMap() {
        Flux<String> stringFlux = Flux
                .fromIterable(stringList)
                .map(name -> name.toUpperCase(Locale.ROOT));

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("ADAM", "ANA", "JACK", "JENNY")
                .verifyComplete();
    }

    @Test
    void transformUsingMap_Length() {
        Flux<Integer> stringFlux = Flux
                .fromIterable(stringList)
                .map(String::length);

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext(4, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    void transformUsingMap_Length_repeat() {
        Flux<Integer> stringFlux = Flux
                .fromIterable(stringList)
                .map(String::length)
                .repeat(1);

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext(4, 3, 4, 5, 4, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    void transformUsingMapFilter() {
        Flux<String> stringFlux = Flux
                .fromIterable(stringList)
                .filter(name -> name.length() > 4)
                .map(String::toUpperCase)
                .repeat(1);

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("JENNY", "JENNY")
                .verifyComplete();
    }

    @Test
    void transformUsingFlatMap() {
        Flux<String> stringFlux = Flux
                .fromIterable(stringList)
                .flatMap(s -> Flux.fromIterable(convertToList(s))) //db or external service call for every element that returns a flux
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNextCount(8)
                .verifyComplete();
    }

    @Test
    void transformUsingFlatMap_usingParallel() {
        Flux<String> stringFlux = Flux
                .fromIterable(stringList)
                .window(2) //Flux<Flux<String>
                .flatMap((s) ->
                        s.map(this::convertToList).subscribeOn(Schedulers.parallel()))// Flux<List<String>>
                .flatMap(Flux::fromIterable)
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNextCount(8)
                .verifyComplete();
    }

    @Test
    void transformUsingFlatMap_usingParallel_maintainOrder() {
        Flux<String> stringFlux = Flux
                .fromIterable(stringList)
                .window(2) //Flux<Flux<String>
                .flatMapSequential((s) ->
                        s.map(this::convertToList).subscribeOn(Schedulers.parallel()))// Flux<List<String>>
                /*.concatMap((s) ->
                        s.map(this::convertToList).subscribeOn(Schedulers.parallel()))// Flux<List<String>>*/
                .flatMap(Flux::fromIterable)
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNextCount(8)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newVal");
    }
}
