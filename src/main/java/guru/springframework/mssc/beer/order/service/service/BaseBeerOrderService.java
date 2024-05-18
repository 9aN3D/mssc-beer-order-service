package guru.springframework.mssc.beer.order.service.service;

import guru.springframework.mssc.beer.order.service.domain.BeerOrder;
import guru.springframework.mssc.beer.order.service.domain.Customer;
import guru.springframework.mssc.beer.order.service.exception.CustomerNotFoundException;
import guru.springframework.mssc.beer.order.service.exception.OrderNotForCustomerException;
import guru.springframework.mssc.beer.order.service.repository.BeerOrderRepository;
import guru.springframework.mssc.beer.order.service.repository.CustomerRepository;
import guru.springframework.mssc.beer.order.service.web.mappers.BeerOrderMapper;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static java.lang.String.format;

@RequiredArgsConstructor
class BaseBeerOrderService {

    protected final BeerOrderRepository beerOrderRepository;
    protected final CustomerRepository customerRepository;
    protected final BeerOrderMapper beerOrderMapper;

    protected Customer getCustomerById(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(format("Customer not found: %s", customerId)));
    }

    protected BeerOrder getOrder(UUID customerId, UUID orderId) {
        getCustomerById(customerId);

        BeerOrder beerOrder = beerOrderRepository.findByIdOrThrow(orderId);
        // fall to exception if customer id's do not match - order not for customer
        if (beerOrder.getCustomer().getId().equals(customerId)) {
            return beerOrder;
        }
        throw new OrderNotForCustomerException(
                format("Order not for customer: {customerId: %s, orderId: %s}", customerId, orderId));
    }

}
