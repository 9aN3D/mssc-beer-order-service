package guru.springframework.mssc.beer.order.service.state.machine.actions;

import guru.cfg.brewery.model.messages.AllocatedOrderEvent;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import guru.springframework.mssc.beer.order.service.events.producers.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationPassedOrderAction implements OrderAction {

    private final EventProducer eventProducer;

    @Override
    public void execute(StateContext<BeerOrderStatus, BeerOrderEventEnum> context) {
        log.trace("Start validation passed order action");

        UUID orderId = getHeaderId(context);
        eventProducer.produceToAllocatedOrderQueue(new AllocatedOrderEvent(orderId));

        log.info("End validation passed order action {id: {}}", orderId);
    }

}
