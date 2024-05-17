package guru.springframework.mssc.beer.order.service.state.machine.actions;

import guru.cfg.brewery.model.BeerOrderDto;
import guru.cfg.brewery.model.events.ValidateOrderRequest;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import guru.springframework.mssc.beer.order.service.events.producers.EventProducer;
import guru.springframework.mssc.beer.order.service.service.BeerOrderManager;
import guru.springframework.mssc.beer.order.service.service.BeerOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateOrderAction implements Action<BeerOrderStatus, BeerOrderEventEnum> {

    private final EventProducer eventProducer;
    private final BeerOrderService beerOrderService;

    @Override
    public void execute(StateContext<BeerOrderStatus, BeerOrderEventEnum> context) {
        log.trace("Validating order action");

        UUID orderId = UUID.fromString((String) context.getMessageHeader(BeerOrderManager.ORDER_ID_HEADER));
        BeerOrderDto beerOrder = beerOrderService.getOrder(orderId);
        eventProducer.produceToValidateOrderRequestQueue(new ValidateOrderRequest(beerOrder));

        log.info("Sent Validation request for order {id: {}}", orderId);
    }

}
