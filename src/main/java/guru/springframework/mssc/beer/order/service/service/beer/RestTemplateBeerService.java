package guru.springframework.mssc.beer.order.service.service.beer;

import guru.springframework.mssc.beer.order.service.service.beer.model.BeerDto;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Profile("!local-discovery")
@Slf4j
@Component
@ConfigurationProperties(prefix = "sfg.brewery", ignoreUnknownFields = false)
public class RestTemplateBeerService implements BeerService {

    private final RestTemplate restTemplate;

    @Setter
    private String beerServiceHost;

    public RestTemplateBeerService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public BeerDto getBeerById(UUID id) {
        return restTemplate.getForObject(beerServiceHost + BEER_PATH_V1 + id.toString(), BeerDto.class);
    }

    @Override
    public BeerDto getBeerByUpc(String upc) {
        return restTemplate.getForObject(beerServiceHost + BEER_UPC_PATH_V1 + upc, BeerDto.class);
    }

}
