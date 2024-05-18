package guru.springframework.mssc.beer.order.service.service.beer;

import guru.springframework.mssc.beer.order.service.service.beer.model.BeerDto;

import java.util.UUID;

public interface BeerService {

    String BEER_PATH_V1 = "/api/v1/beers/";
    String BEER_UPC_PATH_V1 = "/api/v1/beers/upc/";

    BeerDto getBeerById(UUID id);

    BeerDto getBeerByUpc(String upc);

}
