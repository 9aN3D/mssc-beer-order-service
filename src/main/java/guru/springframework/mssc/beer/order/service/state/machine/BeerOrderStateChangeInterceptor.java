package guru.springframework.mssc.beer.order.service.state.machine;

import guru.springframework.mssc.beer.order.service.domain.BeerOrder;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import guru.springframework.mssc.beer.order.service.repository.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static guru.springframework.mssc.beer.order.service.service.BeerOrderManager.ORDER_ID_HEADER;
import static java.util.function.Predicate.not;

@Slf4j
@Component
@RequiredArgsConstructor
public class BeerOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatus, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;

    @Override
    public void preStateChange(State<BeerOrderStatus, BeerOrderEventEnum> state,
                               Message<BeerOrderEventEnum> message,
                               Transition<BeerOrderStatus, BeerOrderEventEnum> transition,
                               StateMachine<BeerOrderStatus, BeerOrderEventEnum> stateMachine) {
        log.trace("Pre state change for {}", state.getId());
        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(ORDER_ID_HEADER, "")))
                .filter(not(String::isEmpty))
                .ifPresent(orderId -> updateStatus(UUID.fromString(orderId), state.getId()));
    }

    private void updateStatus(UUID orderId, BeerOrderStatus status) {
        log.trace("Updating order status {orderId: {}, status: {}}", orderId, status);

        BeerOrder beerOrder = beerOrderRepository.findByIdOrThrow(orderId);
        beerOrder.setOrderStatus(status);
        BeerOrder beerOrderSaved = beerOrderRepository.saveAndFlush(beerOrder);

        log.info("Updated order status {orderId: {}, result: {}}", orderId, beerOrderSaved);
    }

}
