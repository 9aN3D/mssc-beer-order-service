package guru.springframework.mssc.beer.order.service.state.machine.actions;

import guru.cfg.brewery.model.BeerOrderDto;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import guru.springframework.mssc.beer.order.service.service.BeerOrderQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.ALLOCATED_ORDER;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.VALIDATED;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidatedOrderStateAction implements OrderAction {

    private final BeerOrderQueryService beerOrderQueryService;

    @Override
    public void execute(StateContext<BeerOrderStatus, BeerOrderEventEnum> context) {
        log.trace("Start ALLOCATED_ORDER action");

        UUID orderId = getHeaderId(context);
        BeerOrderDto beerOrder = beerOrderQueryService.getOrderWithRetryPolicy(orderId, VALIDATED);
        context.getStateMachine().sendEvent(MessageBuilder.withPayload(ALLOCATED_ORDER)
                .setHeader(getHeaderName(), beerOrder.getId().toString())
                .build());

        log.info("End ALLOCATED_ORDER action {id: {}}", orderId);
    }

}
