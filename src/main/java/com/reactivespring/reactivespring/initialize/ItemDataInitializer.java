package com.reactivespring.reactivespring.initialize;

import com.reactivespring.reactivespring.document.Item;
import com.reactivespring.reactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test-case")
public class ItemDataInitializer implements CommandLineRunner {
    @Autowired
    ItemReactiveRepository itemReactiveRepository;
    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
    }

    private void initialDataSetup() {
        List<Item> itemList = Arrays.asList(new Item(null, "Samsung TV", 400.0),
                new Item(null, "LG TV", 420.0),
                new Item(null, "Apple TV", 299.99),
                new Item(null, "Oppo TV", 149.99),
                new Item("1", "OnePlus TV", 49.99));

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(System.out::println);
    }
}
