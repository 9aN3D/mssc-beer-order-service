package guru.springframework.mssc.beer.order.service.service;

import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public interface BeerOrderFacade extends BeerOrderService, BeerOrderQueryService {

    @Service
    @RequiredArgsConstructor
    class DefaultBeerOrderFacade implements BeerOrderFacade {

        @Delegate
        private final BeerOrderQueryService beerOrderQueryService;
        @Delegate
        private final BeerOrderService beerOrderService;

    }

}
