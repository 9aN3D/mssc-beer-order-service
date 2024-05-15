package guru.springframework.mssc.beer.order.service.service.beer;

import guru.springframework.mssc.beer.order.service.service.beer.model.BeerDto;

import java.util.UUID;

public interface BeerService {

    BeerDto getBeerById(UUID id);

    BeerDto getBeerByUpc(String upc);

}
