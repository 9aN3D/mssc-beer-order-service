package guru.springframework.mssc.beer.order.service.service;

import guru.springframework.mssc.beer.order.service.domain.BeerOrder;

public interface BeerOrderManager {

    String ORDER_ID_HEADER = "ORDER_ID_HEADER";

    BeerOrder newBeerOrder(BeerOrder beerOrder);

}
