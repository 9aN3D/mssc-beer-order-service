package guru.springframework.mssc.beer.order.service.service;

import guru.springframework.mssc.beer.order.service.domain.BeerOrder;

public interface BeerOrderManager {

    BeerOrder newBeerOrder(BeerOrder beerOrder);

}
