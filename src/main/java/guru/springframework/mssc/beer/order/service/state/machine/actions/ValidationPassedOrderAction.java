package guru.springframework.mssc.beer.order.service.state.machine.actions;

import guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.ALLOCATED_ORDER;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationPassedOrderAction implements OrderAction {

    @Override
    public void execute(StateContext<BeerOrderStatus, BeerOrderEventEnum> context) {
        log.trace("Start validation passed order action");

        UUID orderId = getHeaderId(context);
        context.getStateMachine().sendEvent(MessageBuilder.withPayload(ALLOCATED_ORDER)
                .setHeader(getHeaderName(), orderId.toString())
                .build());

        log.info("End validation passed order action {id: {}}", orderId);
    }

}
