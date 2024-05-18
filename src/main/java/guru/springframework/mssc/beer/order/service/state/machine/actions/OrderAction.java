package guru.springframework.mssc.beer.order.service.state.machine.actions;

import guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.util.UUID;

import static guru.springframework.mssc.beer.order.service.service.BeerOrderManager.ORDER_ID_HEADER;
import static java.util.Objects.requireNonNull;

public interface OrderAction extends Action<BeerOrderStatus, BeerOrderEventEnum> {

    default UUID getHeaderId(StateContext<BeerOrderStatus, BeerOrderEventEnum> context) {
        return requireNonNull(UUID.fromString((String) context.getMessageHeader(getHeaderName())));
    }

    default String getHeaderName() {
        return ORDER_ID_HEADER;
    }

}
