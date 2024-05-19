package guru.springframework.mssc.beer.order.service.service;

import guru.cfg.brewery.model.BeerOrderDto;
import guru.springframework.mssc.beer.order.service.domain.BeerOrder;
import guru.springframework.mssc.beer.order.service.domain.Customer;
import guru.springframework.mssc.beer.order.service.repository.BeerOrderRepository;
import guru.springframework.mssc.beer.order.service.repository.CustomerRepository;
import guru.springframework.mssc.beer.order.service.web.mappers.BeerOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.NEW;

@Slf4j
@Primary
@Service
@Transactional
class RepositoryBeerOrderService extends BaseBeerOrderService implements BeerOrderService {

    private final BeerOrderManager beerOrderManager;

    public RepositoryBeerOrderService(BeerOrderRepository beerOrderRepository,
                                      CustomerRepository customerRepository,
                                      BeerOrderMapper beerOrderMapper,
                                      BeerOrderManager beerOrderManager) {
        super(beerOrderRepository, customerRepository, beerOrderMapper);
        this.beerOrderManager = beerOrderManager;
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

        BeerOrder newBeerOrder = beerOrderManager.processNewBeerOrder(beerOrder);
        BeerOrderDto result = beerOrderMapper.beerOrderToDto(newBeerOrder);

        log.info("Placed order {beer order id: {}}", result.getId());
        return result;
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        log.trace("Picking order {customerId: {}, orderId: {}}", customerId, orderId);

        BeerOrder beerOrder = getOrder(customerId, orderId);
        //TODO verify actual status, must be ALLOCATED
        beerOrderManager.processPickup(beerOrder.getId());

        log.info("Picked order {id: {}}", beerOrder.getId());
    }

}
