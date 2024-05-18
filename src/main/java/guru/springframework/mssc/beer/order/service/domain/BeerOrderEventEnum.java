package guru.springframework.mssc.beer.order.service.domain;

public enum BeerOrderEventEnum {

    VALIDATED_ORDER,
    VALIDATION_PASSED,
    VALIDATION_FAILED,

    ALLOCATED_ORDER,
    ALLOCATION_SUCCESS,
    ALLOCATION_NO_INVENTORY,
    ALLOCATION_FAILED,

    CANCELED_ORDER,
    BEER_ORDER_PICKED_UP

}
