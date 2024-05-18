package guru.springframework.mssc.beer.order.service.service;

import guru.cfg.brewery.model.BeerOrderDto;
import guru.cfg.brewery.model.BeerOrderPagedList;
import guru.springframework.mssc.beer.order.service.domain.BeerOrder;
import guru.springframework.mssc.beer.order.service.domain.Customer;
import guru.springframework.mssc.beer.order.service.repository.BeerOrderRepository;
import guru.springframework.mssc.beer.order.service.repository.CustomerRepository;
import guru.springframework.mssc.beer.order.service.web.mappers.BeerOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Slf4j
@Primary
@Service
@Transactional(readOnly = true)
class RepositoryBeerOrderQueryService extends BaseBeerOrderService implements BeerOrderQueryService {

    public RepositoryBeerOrderQueryService(BeerOrderRepository beerOrderRepository,
                                           CustomerRepository customerRepository,
                                           BeerOrderMapper beerOrderMapper) {
        super(beerOrderRepository, customerRepository, beerOrderMapper);
    }

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
    public BeerOrderDto getOrderById(UUID customerId, UUID orderId) {
        log.trace("Getting order {customerId: {}, orderId: {}}", customerId, orderId);

        BeerOrderDto beerOrderDto = beerOrderMapper.beerOrderToDto(getOrder(customerId, orderId));

        log.info("Got order {beerOrderDto: {}}", beerOrderDto);
        return beerOrderDto;
    }

    @Override
    public BeerOrderDto getOrder(UUID orderId) {
        log.trace("Getting order {orderId: {}}", orderId);

        BeerOrderDto result = beerOrderMapper.beerOrderToDto(beerOrderRepository.findByIdOrThrow(orderId));

        log.info("Got order {result: {}}", result);
        return result;
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

}
