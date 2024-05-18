package guru.springframework.mssc.beer.order.service.state.machine;

import guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.ALLOCATED_ORDER;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.ALLOCATION_FAILED;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.ALLOCATION_NO_INVENTORY;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.ALLOCATION_SUCCESS;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.VALIDATED_ORDER;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.VALIDATION_FAILED;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.VALIDATION_PASSED;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.ALLOCATED;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.ALLOCATION_EXCEPTION;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.ALLOCATION_PENDING;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.DELIVERED;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.DELIVERY_EXCEPTION;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.NEW;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.PENDING_INVENTORY;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.PICKED_UP;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.VALIDATED;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.VALIDATION_EXCEPTION;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.VALIDATION_PENDING;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableStateMachineFactory
public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatus, BeerOrderEventEnum> {

    private final Action<BeerOrderStatus, BeerOrderEventEnum> validateOrderAction;
    private final Action<BeerOrderStatus, BeerOrderEventEnum> validationPassedOrderAction;
    private final Action<BeerOrderStatus, BeerOrderEventEnum> allocateOrderAction;

    @Override
    public void configure(StateMachineStateConfigurer<BeerOrderStatus, BeerOrderEventEnum> states) throws Exception {
        states.withStates()
                .initial(NEW)
                .states(EnumSet.allOf(BeerOrderStatus.class))
                .end(PICKED_UP)
                .end(DELIVERED)
                .end(DELIVERY_EXCEPTION)
                .end(VALIDATION_EXCEPTION)
                .end(ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BeerOrderStatus, BeerOrderEventEnum> transitions) throws Exception {
        transitions.withExternal().source(NEW).target(VALIDATION_PENDING)
                .event(VALIDATED_ORDER)
                .action(validateOrderAction)
                .and()
                .withExternal().source(VALIDATION_PENDING).target(VALIDATED)
                .event(VALIDATION_PASSED)
                .action(validationPassedOrderAction)
                .and()
                .withExternal().source(VALIDATION_PENDING).target(VALIDATION_EXCEPTION)
                .event(VALIDATION_FAILED)
                .and()
                .withExternal().source(VALIDATED).target(ALLOCATION_PENDING)
                .event(ALLOCATED_ORDER)
                .action(allocateOrderAction)
                .and()
                .withExternal().source(ALLOCATION_PENDING).target(ALLOCATED)
                .event(ALLOCATION_SUCCESS)
                .and()
                .withExternal().source(ALLOCATION_PENDING).target(ALLOCATION_EXCEPTION)
                .event(ALLOCATION_FAILED)
                .and()
                .withExternal().source(ALLOCATION_PENDING).target(PENDING_INVENTORY)
                .event(ALLOCATION_NO_INVENTORY);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<BeerOrderStatus, BeerOrderEventEnum> config) throws Exception {
        StateMachineListenerAdapter<BeerOrderStatus, BeerOrderEventEnum> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<BeerOrderStatus, BeerOrderEventEnum> from, State<BeerOrderStatus, BeerOrderEventEnum> to) {
                log.trace("BeerOrderState changed from {}, to {}", from, to);
            }
        };
        config.withConfiguration().listener(adapter);
    }

}
