package com.reactivespring.reactivespring.handler;

import com.reactivespring.reactivespring.constants.ItemConstants;
import com.reactivespring.reactivespring.document.Item;
import com.reactivespring.reactivespring.repository.ItemReactiveRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test-case")
class ItemsHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @BeforeEach
    void setUp() {
        List<Item> itemList = Arrays.asList(
                new Item(null, "Samsung TV", 400.0),
                new Item(null, "LG TV", 420.0),
                new Item(null, "Apple TV", 299.99),
                new Item(null, "Oppo TV", 149.99),
                new Item("1", "OnePlus TV", 49.99));

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    void getAllItems() {
        itemReactiveRepository.findAll();
        webTestClient.get()
                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    void getAllItems_approach2() {
        itemReactiveRepository.findAll();
        webTestClient.get()
                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith((response) -> {
                    List<Item>  itemList = response.getResponseBody();
                    itemList.forEach((item -> {
                        Assertions.assertNotNull(item.getId());
                    }));
                });
    }

    @Test
    void getAllItems_approach3() {
        itemReactiveRepository.findAll();
        Flux<Item> responseBody = webTestClient.get()
                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void getOneItem() {
        webTestClient.get()
                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/{id}"), "1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.price", 49.99);
    }

    @Test
    void getOneItem_notFound() {
        webTestClient.get()
                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/{id}"), 2)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createItem() {
        Item item = new Item(null, "Dell TV", 1000.0);
        webTestClient.post()
                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1)
                .body(Mono.just(item), Item.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.price").isEqualTo(1000.0)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Dell TV");
    }

    @Test
    void deleteOneItem() {
        webTestClient.delete()
                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/{id}"), "1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    void updateItem() {
        Item item = new Item(null, "Dell TV", 1000.0);
        webTestClient.put()
                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/{id}"), "1")
                .body(Mono.just(item), Item.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.price").isEqualTo(1000.0)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Dell TV");
    }

    @Test
    void updateItem_notFound() {
        Item item = new Item(null, "Dell TV", 1000.0);
        webTestClient.put()
                .uri(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/{id}"), "2")
                .body(Mono.just(item), Item.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
