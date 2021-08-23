package com.reactivespring.reactivespring.router;

import com.reactivespring.reactivespring.constants.ItemConstants;
import com.reactivespring.reactivespring.handler.ItemsHandler;
import com.reactivespring.reactivespring.handler.SampleHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ItemsRouter {

    @Bean("itemRoute")
    public RouterFunction<ServerResponse> itemRoute(ItemsHandler itemsHandler) {

        return RouterFunctions
                .route(RequestPredicates.GET(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1)
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)
                        ), itemsHandler:: getAllItems)
                .andRoute(RequestPredicates.GET(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/{id}"))
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)
                        ), itemsHandler:: getOneItem)
                .andRoute(RequestPredicates.POST(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1)
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)
                        ), itemsHandler:: createItem)
                .andRoute(RequestPredicates.DELETE(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/{id}"))
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)
                        ), itemsHandler:: deleteItem)
                .andRoute(RequestPredicates.PUT(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/{id}"))
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)
                        ), itemsHandler:: updateOneItem);
    }

    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/functional/runtimeException")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)
                        ), itemsHandler:: itemEx);
    }
}
