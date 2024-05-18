package guru.springframework.mssc.beer.order.service.state.machine.actions;

import guru.cfg.brewery.model.BeerOrderDto;
import guru.cfg.brewery.model.messages.AllocateOrderRequest;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import guru.springframework.mssc.beer.order.service.events.producers.EventProducer;
import guru.springframework.mssc.beer.order.service.service.BeerOrderManager;
import guru.springframework.mssc.beer.order.service.service.BeerOrderQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocateOrderAction implements OrderAction {

    private final EventProducer eventProducer;
    private final BeerOrderQueryService beerOrderQueryService;

    @Override
    public void execute(StateContext<BeerOrderStatus, BeerOrderEventEnum> context) {
        log.trace("Allocating order action");

        UUID orderId = getHeaderId(context);
        BeerOrderDto beerOrder = beerOrderQueryService.getOrder(orderId);
        eventProducer.produceToAllocateOrderQueue(new AllocateOrderRequest(beerOrder));

        log.info("Sent Allocation request for order {id: {}}", orderId);
    }

}
