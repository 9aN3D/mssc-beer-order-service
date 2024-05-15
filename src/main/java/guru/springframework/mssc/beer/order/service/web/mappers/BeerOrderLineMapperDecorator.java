package guru.springframework.mssc.beer.order.service.web.mappers;

import guru.springframework.mssc.beer.order.service.domain.BeerOrderLine;
import guru.springframework.mssc.beer.order.service.service.beer.BeerService;
import guru.springframework.mssc.beer.order.service.service.beer.model.BeerDto;
import guru.springframework.mssc.beer.order.service.web.model.BeerOrderLineDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class BeerOrderLineMapperDecorator implements BeerOrderLineMapper {

    private BeerOrderLineMapper beerOrderLineMapper;
    private BeerService beerService;

    @Autowired
    public void setBeerOrderLineMapper(@Qualifier("delegate")BeerOrderLineMapper beerOrderLineMapper) {
        this.beerOrderLineMapper = beerOrderLineMapper;
    }

    @Autowired
    public void setBeerService(BeerService beerService) {
        this.beerService = beerService;
    }

    @Override
    public BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line) {
        BeerOrderLineDto beerOrderLine = beerOrderLineMapper.beerOrderLineToDto(line);
        BeerDto beer = beerService.getBeerByUpc(line.getUpc());
        beerOrderLine.setBeerId(beer.getId());
        beerOrderLine.setBeerName(beer.getName());
        beerOrderLine.setBeerStyle(beer.getStyle());
        beerOrderLine.setPrice(beer.getPrice());
        return beerOrderLine;
    }

}
