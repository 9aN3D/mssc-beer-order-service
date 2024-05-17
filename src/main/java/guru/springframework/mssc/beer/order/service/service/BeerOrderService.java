package guru.springframework.mssc.beer.order.service.service;

import guru.cfg.brewery.model.BeerOrderDto;
import guru.cfg.brewery.model.BeerOrderPagedList;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BeerOrderService {

    BeerOrderPagedList listOrders(UUID customerId, Pageable pageable);

    BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto);

    BeerOrderDto getOrderById(UUID customerId, UUID orderId);

    BeerOrderDto getOrder(UUID orderId);

    void pickupOrder(UUID customerId, UUID orderId);

    void updateStatus(UUID orderId, BeerOrderStatus status);

}
