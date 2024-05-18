package guru.springframework.mssc.beer.order.service.service;

import guru.cfg.brewery.model.BeerOrderDto;
import guru.cfg.brewery.model.BeerOrderPagedList;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BeerOrderQueryService {

    BeerOrderPagedList listOrders(UUID customerId, Pageable pageable);

    BeerOrderDto getOrderById(UUID customerId, UUID orderId);

    BeerOrderDto getOrder(UUID orderId);

}
