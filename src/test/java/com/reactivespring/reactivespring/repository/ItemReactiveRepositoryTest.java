package com.reactivespring.reactivespring.repository;

import com.reactivespring.reactivespring.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(new Item(null, "Samsung TV", 400.0),
            new Item(null, "LG TV", 420.0),
            new Item(null, "Apple TV", 299.99),
            new Item(null, "Oppo TV", 149.99),
            new Item("1", "OnePlus TV", 49.99));

    @BeforeEach
    void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    void getAllItems() {
        Flux<Item> all = itemReactiveRepository.findAll();
        StepVerifier
                .create(all.log())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void getItem() {
        Mono<Item> itemMono = itemReactiveRepository.findById("1");
        StepVerifier
                .create(itemMono.log())
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("OnePlus TV"))
                .verifyComplete();
    }

    @Test
    void getItemByDescription() {
        Flux<Item> itemFlux = itemReactiveRepository.findByDescription("OnePlus TV");
        StepVerifier
                .create(itemFlux.log())
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("OnePlus TV"))
                .verifyComplete();
    }

    @Test
    void saveItem() {
        Mono<Item> itemMono = itemReactiveRepository.save(new Item("2", "Google TV", 9.99));
        StepVerifier
                .create(itemMono.log())
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Google TV"))
                .verifyComplete();
    }

    @Test
    void updateItem() {
        Flux<Item> itemFlux = itemReactiveRepository.findByDescription("OnePlus TV")
                .map(item -> {
                    item.setPrice(1.99);
                    return item;
                })
                .flatMap(item -> itemReactiveRepository.save(item));
        StepVerifier
                .create(itemFlux.log())
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice().equals(1.99))
                .verifyComplete();
    }

    @Test
    void deleteItemById() {
        Mono<Void> voidMono = itemReactiveRepository.findById("1")
                .map(Item::getId)
                .flatMap(id -> itemReactiveRepository.deleteById(id));
        StepVerifier
                .create(voidMono.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier
                .create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void deleteItem() {
        Flux<Void> voidFlux = itemReactiveRepository.findByDescription("OnePlus TV")
                .flatMap(item -> itemReactiveRepository.delete(item));
        StepVerifier
                .create(voidFlux.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier
                .create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }
}
