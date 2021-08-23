package com.reactivespring.reactivespring.controller.v1;

import com.reactivespring.reactivespring.constants.ItemConstants;
import com.reactivespring.reactivespring.document.Item;
import com.reactivespring.reactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @GetMapping(value = ItemConstants.ITEM_ENDPOINT_V1)
    public Flux<Item> getAllItems() {
        return itemReactiveRepository.findAll();
    }

    @GetMapping(value = ItemConstants.ITEM_ENDPOINT_V1 + "/runtimeException")
    public Flux<Item> runTimeException() {
        return itemReactiveRepository.findAll()
                .concatWith( Mono.error(new RuntimeException("RuntimeException Occurred")));
    }

    @GetMapping(value = ItemConstants.ITEM_ENDPOINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id) {

        return itemReactiveRepository.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = ItemConstants.ITEM_ENDPOINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item) {
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(value = ItemConstants.ITEM_ENDPOINT_V1 + "/{id}")
    public Mono<Void> deleteItemById(@PathVariable String id) {
        return itemReactiveRepository.deleteById(id);
    }

    @PutMapping(value = ItemConstants.ITEM_ENDPOINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item item) {
        return itemReactiveRepository.findById(id)
                .flatMap(item1 -> {
                    item1.setPrice(item.getPrice());
                    item1.setDescription(item.getDescription());
                    return itemReactiveRepository.save(item1);
                })
                .map(item1 -> new ResponseEntity<>(item1, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
