package guru.springframework.mssc.beer.order.service.state.machine;

import guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import guru.springframework.mssc.beer.order.service.service.BeerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static guru.springframework.mssc.beer.order.service.service.DefaultBeerOrderManager.ORDER_ID_HEADER;

@Component
@RequiredArgsConstructor
public class BeerOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatus, BeerOrderEventEnum> {

    private final BeerOrderService beerOrderService;

    @Override
    public void preStateChange(State<BeerOrderStatus, BeerOrderEventEnum> state,
                               Message<BeerOrderEventEnum> message,
                               Transition<BeerOrderStatus, BeerOrderEventEnum> transition,
                               StateMachine<BeerOrderStatus, BeerOrderEventEnum> stateMachine) {
        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(ORDER_ID_HEADER, "")))
                .filter(String::isEmpty)
                .ifPresent(orderId -> beerOrderService.updateStatus(UUID.fromString(orderId), state.getId()));
    }

}
