package guru.springframework.mssc.beer.order.service.service;

import guru.cfg.brewery.model.BeerOrderDto;
import guru.springframework.mssc.beer.order.service.domain.BeerOrder;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import guru.springframework.mssc.beer.order.service.domain.Customer;
import guru.springframework.mssc.beer.order.service.repository.BeerOrderRepository;
import guru.springframework.mssc.beer.order.service.repository.CustomerRepository;
import guru.springframework.mssc.beer.order.service.web.mappers.BeerOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.NEW;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.PICKED_UP;

@Slf4j
@Primary
@Service
@Transactional
class RepositoryBeerOrderService extends BaseBeerOrderService implements BeerOrderService {

    private final ApplicationEventPublisher publisher;

    public RepositoryBeerOrderService(BeerOrderRepository beerOrderRepository,
                                      CustomerRepository customerRepository,
                                      BeerOrderMapper beerOrderMapper,
                                      ApplicationEventPublisher publisher) {
        super(beerOrderRepository, customerRepository, beerOrderMapper);
        this.publisher = publisher;
    }

    @Override
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
    public void pickupOrder(UUID customerId, UUID orderId) {
        log.trace("Picking order {customerId: {}, orderId: {}}", customerId, orderId);

        BeerOrder beerOrder = getOrder(customerId, orderId);
        beerOrder.setOrderStatus(PICKED_UP);

        BeerOrder beerOrderSaved = beerOrderRepository.save(beerOrder);
        log.info("Picked order {id: {}}", beerOrderSaved.getId());
    }

    @Override
    public void updateStatus(UUID orderId, BeerOrderStatus status) {
        log.trace("Updating order status {orderId: {}, status: {}}", orderId, status);

        BeerOrder beerOrder = beerOrderRepository.findByIdOrThrow(orderId);
        beerOrder.setOrderStatus(status);
        BeerOrder beerOrderSaved = beerOrderRepository.saveAndFlush(beerOrder);

        log.info("Updated order status {orderId: {}, result: {}}", orderId, beerOrderSaved);
    }

}
