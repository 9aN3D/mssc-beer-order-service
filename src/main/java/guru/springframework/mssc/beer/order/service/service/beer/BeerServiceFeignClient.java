package guru.springframework.mssc.beer.order.service.service.beer;

import guru.springframework.mssc.beer.order.service.service.beer.model.BeerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient(name = "beer-service", url = "${sfg.brewery.beer-service-host}")
public interface BeerServiceFeignClient {

    @RequestMapping(method = GET, value = BeerService.BEER_PATH_V1)
    BeerDto getBeerById(@PathVariable UUID id);

    @RequestMapping(method = GET, value = BeerService.BEER_UPC_PATH_V1)
    BeerDto getBeerByUpc(@PathVariable String upc);

}
