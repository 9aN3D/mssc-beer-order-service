package guru.springframework.mssc.beer.order.service.state.machine.actions;

import guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class ValidationFailureAction implements OrderAction {

    @Override
    public void execute(StateContext<BeerOrderStatus, BeerOrderEventEnum> context) {
        log.trace("Validating failure action");
        UUID orderId = getHeaderId(context);
        log.error("Compensation Transaction... Validation Failed: {}", orderId);
    }

}
