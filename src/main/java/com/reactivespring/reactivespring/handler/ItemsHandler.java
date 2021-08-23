package com.reactivespring.reactivespring.handler;

import com.reactivespring.reactivespring.document.Item;
import com.reactivespring.reactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ItemsHandler {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest) {

        Mono<Item> byId = itemReactiveRepository.findById(
                serverRequest.pathVariable("id"));
        return byId.flatMap(item ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(item)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
        Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);
        return itemMono.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(itemReactiveRepository.save(item), Item.class));
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        Mono<Void> byId = itemReactiveRepository.deleteById(
                serverRequest.pathVariable("id"));
        return
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(byId, Void.class)
                        .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> updateOneItem(ServerRequest serverRequest) {

        Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);

        Mono<Item> itemMono2 = itemMono.flatMap((item) -> itemReactiveRepository.findById(
                        serverRequest.pathVariable("id"))
                .flatMap(currentItem -> {
                    currentItem.setPrice(item.getPrice());
                    currentItem.setDescription(item.getDescription());
                    return itemReactiveRepository.save(currentItem);
                }));

        return itemMono2.flatMap(item ->
                ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(item)))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> itemEx(ServerRequest serverRequest) {

        throw new RuntimeException("RuntimeException occurred");
    }
}
