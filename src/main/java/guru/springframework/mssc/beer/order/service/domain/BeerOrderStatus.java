package guru.springframework.mssc.beer.order.service.domain;

public enum BeerOrderStatus {

    NEW,
    VALIDATED,
    VALIDATION_PENDING,
    VALIDATION_EXCEPTION,
    ALLOCATION_PENDING,
    ALLOCATED,
    ALLOCATION_EXCEPTION,
    CANCELLED,
    PENDING_INVENTORY,
    PICKED_UP,
    DELIVERED,
    DELIVERY_EXCEPTION

}
