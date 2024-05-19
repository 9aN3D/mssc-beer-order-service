package guru.springframework.mssc.beer.order.service.service;

import guru.cfg.brewery.model.BeerOrderDto;

import java.util.UUID;

public interface BeerOrderService {

    BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto);

    void pickupOrder(UUID customerId, UUID orderId);

}
