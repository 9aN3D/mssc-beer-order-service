package guru.springframework.mssc.beer.order.service.service;

import guru.springframework.mssc.beer.order.service.domain.BeerOrder;
import guru.springframework.mssc.beer.order.service.domain.Customer;
import guru.springframework.mssc.beer.order.service.exception.BeerOrderNotFoundException;
import guru.springframework.mssc.beer.order.service.exception.CustomerNotFoundException;
import guru.springframework.mssc.beer.order.service.exception.OrderNotForCustomerException;
import guru.springframework.mssc.beer.order.service.repository.BeerOrderRepository;
import guru.springframework.mssc.beer.order.service.repository.CustomerRepository;
import guru.springframework.mssc.beer.order.service.web.mappers.BeerOrderMapper;
import guru.springframework.mssc.beer.order.service.web.model.BeerOrderDto;
import guru.springframework.mssc.beer.order.service.web.model.BeerOrderPagedList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.NEW;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.PICKED_UP;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@AllArgsConstructor
public class RepositoryBeerOrderService implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final ApplicationEventPublisher publisher;

    @Override
    public BeerOrderPagedList listOrders(UUID customerId, Pageable pageable) {
        log.trace("Getting list orders {customerId: {}}", customerId);

        Customer customer = getCustomerById(customerId);

        Page<BeerOrder> beerOrderPage = beerOrderRepository.findAllByCustomer(customer, pageable);
        log.info("Got list orders {total elements: {}}", beerOrderPage.getTotalElements());
        return new BeerOrderPagedList(
                collectBeerOrderDto(beerOrderPage),
                buildPageRequest(beerOrderPage),
                beerOrderPage.getTotalElements());
    }

    @Override
    @Transactional
    public BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto) {
        log.trace("Placing order {customerID: {}, beerOrderDto: {}}", customerId, beerOrderDto);
        Customer customer = getCustomerById(customerId);
        BeerOrder beerOrder = beerOrderMapper.dtoToBeerOrder(beerOrderDto);
        beerOrder.setId(null);
        beerOrder.setCustomer(customer);
        beerOrder.setOrderStatus(NEW);

        beerOrder.getBeerOrderLines().forEach(line -> line.setBeerOrder(beerOrder));

        BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);
        BeerOrderDto savedBeerOrderDto = beerOrderMapper.beerOrderToDto(savedBeerOrder);
        log.info("Placed order {beer order id: {}}", savedBeerOrderDto.getId());
        return savedBeerOrderDto;
    }

    @Override
    public BeerOrderDto getOrderById(UUID customerId, UUID orderId) {
        log.trace("Getting order {customerId: {}, orderId: {}}", customerId, orderId);
        BeerOrderDto beerOrderDto = beerOrderMapper.beerOrderToDto(getOrder(customerId, orderId));
        log.info("Got order {beerOrderDto: {}}", beerOrderDto);
        return beerOrderDto;
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        log.trace("Picking order {customerId: {}, orderId: {}}", customerId, orderId);
        BeerOrder beerOrder = getOrder(customerId, orderId);
        beerOrder.setOrderStatus(PICKED_UP);

        BeerOrder beerOrderSaved = beerOrderRepository.save(beerOrder);
        log.info("Picked order {id: {}}", beerOrderSaved.getId());
    }

    private Customer getCustomerById(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(format("Customer not found: %s", customerId)));
    }

    private List<BeerOrderDto> collectBeerOrderDto(Page<BeerOrder> beerOrderPage) {
        return beerOrderPage.stream()
                .map(beerOrderMapper::beerOrderToDto)
                .collect(toList());
    }

    private Pageable buildPageRequest(Page<BeerOrder> beerOrderPage) {
        return PageRequest.of(
                beerOrderPage.getPageable().getPageNumber(),
                beerOrderPage.getPageable().getPageSize());
    }

    private BeerOrder getOrder(UUID customerId, UUID orderId) {
        getCustomerById(customerId);

        BeerOrder beerOrder = getBeerOrderById(orderId);
        // fall to exception if customer id's do not match - order not for customer
        if (beerOrder.getCustomer().getId().equals(customerId)) {
            return beerOrder;
        }
        throw new OrderNotForCustomerException(
                format("Order not for customer: {customerId: %s, orderId: %s}", customerId, orderId));
    }

    private BeerOrder getBeerOrderById(UUID orderId) {
        return beerOrderRepository.findById(orderId)
                .orElseThrow(() -> new BeerOrderNotFoundException(format("Beer order not found: %s", orderId)));
    }

}
