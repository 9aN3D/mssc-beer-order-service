package guru.springframework.mssc.beer.order.service.service.beer;

import guru.springframework.mssc.beer.order.service.service.beer.model.BeerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Profile("local-discovery")
@Slf4j
@Component
@RequiredArgsConstructor
class FeignClientBeerService implements BeerService {

    private final BeerServiceFeignClient beerServiceFeignClient;

    @Override
    public BeerDto getBeerById(UUID id) {
        return beerServiceFeignClient.getBeerById(id);
    }

    @Override
    public BeerDto getBeerByUpc(String upc) {
        return beerServiceFeignClient.getBeerByUpc(upc);
    }

}
