package guru.springframework.mssc.beer.order.service.service;

import guru.cfg.brewery.model.BeerOrderDto;
import guru.springframework.mssc.beer.order.service.domain.BeerOrder;

import java.util.UUID;

public interface BeerOrderManager {

    String ORDER_ID_HEADER = "ORDER_ID_HEADER";

    BeerOrder processNewBeerOrder(BeerOrder beerOrder);

    void processValidationResult(UUID orderId, Boolean isValid);

    void processAllocationFailed(UUID orderId);

    void processAllocationPendingInventory(BeerOrderDto beerOrder);

    void processAllocationPassed(BeerOrderDto beerOrder);

    void processPickup(UUID orderId);

}
