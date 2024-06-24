package guru.springframework.mssc.beer.order.service.service;

import guru.cfg.brewery.model.BeerOrderDto;
import guru.cfg.brewery.model.BeerOrderPagedList;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.util.UUID;

public interface BeerOrderQueryService {

    BeerOrderPagedList listOrders(UUID customerId, Pageable pageable);

    BeerOrderDto getOrderById(UUID customerId, UUID orderId);

    BeerOrderDto getOrder(UUID orderId);

    BeerOrderDto getOrderWithRetryPolicy(UUID orderId, BeerOrderStatus status, int maxAttempts, Duration delay);

    default BeerOrderDto getOrderWithRetryPolicy(UUID orderId, BeerOrderStatus status, int maxAttempts) {
        return getOrderWithRetryPolicy(orderId, status, maxAttempts, Duration.ofSeconds(2));
    }

    default BeerOrderDto getOrderWithRetryPolicy(UUID orderId, BeerOrderStatus status) {
        return getOrderWithRetryPolicy(orderId, status, 3, Duration.ofSeconds(2));
    }

}
