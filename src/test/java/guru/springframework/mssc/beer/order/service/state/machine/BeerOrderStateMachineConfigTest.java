package guru.springframework.mssc.beer.order.service.state.machine;

import guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.VALIDATED_ORDER;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.VALIDATION_FAILED;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.VALIDATION_PASSED;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.VALIDATED;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.VALIDATION_EXCEPTION;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.VALIDATION_PENDING;
import static guru.springframework.mssc.beer.order.service.service.BeerOrderManager.ORDER_ID_HEADER;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestPropertySource(properties = "app.scheduling.enable=false")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BeerOrderStateMachineConfigTest {

    @Autowired
    private StateMachineFactory<BeerOrderStatus, BeerOrderEventEnum> stateMachineFactory;

    @MockBean
    @Qualifier("validateOrderAction")
    private Action<BeerOrderStatus, BeerOrderEventEnum> validateOrderAction;
    @MockBean
    @Qualifier("validatedOrderStateAction")
    private Action<BeerOrderStatus, BeerOrderEventEnum> validatedOrderStateAction;

    private final UUID ORDER_ID = UUID.randomUUID();

    @Test
    void shouldBeNewWhenStarted() throws Exception {
        StateMachine<BeerOrderStatus, BeerOrderEventEnum> stateMachine = stateMachineFactory.getStateMachine();

        StateMachineTestPlan<BeerOrderStatus, BeerOrderEventEnum> testPlan =
                StateMachineTestPlanBuilder.<BeerOrderStatus, BeerOrderEventEnum>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectStateMachineStarted(1)
                        .expectState(BeerOrderStatus.NEW)
                        .and().build();

        testPlan.test();
    }

    @Test
    void shouldBeValidationPendingWhenSendValidatedOrder() throws Exception {
        StateMachine<BeerOrderStatus, BeerOrderEventEnum> stateMachine = stateMachineFactory.getStateMachine();

        StateMachineTestPlan<BeerOrderStatus, BeerOrderEventEnum> testPlan =
                StateMachineTestPlanBuilder.<BeerOrderStatus, BeerOrderEventEnum>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectStateMachineStarted(1)
                        .expectState(BeerOrderStatus.NEW)
                        .and().step()
                        .sendEvent(buildEvent(VALIDATED_ORDER))
                        .expectStateChanged(1)
                        .expectState(VALIDATION_PENDING)
                        .and().build();

        testPlan.test();

        verify(validateOrderAction, times(1)).execute(argThat(new StateContextArgsMatcher(ORDER_ID)));
    }

    @Test
    void shouldBeValidationPassedWhenSendValidationPassed() throws Exception {
        StateMachine<BeerOrderStatus, BeerOrderEventEnum> stateMachine = stateMachineFactory.getStateMachine();

        StateMachineTestPlan<BeerOrderStatus, BeerOrderEventEnum> testPlan =
                StateMachineTestPlanBuilder.<BeerOrderStatus, BeerOrderEventEnum>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectStateMachineStarted(1)
                        .expectState(BeerOrderStatus.NEW)
                        .and().step()
                        .sendEvent(buildEvent(VALIDATED_ORDER))
                        .expectStateChanged(1)
                        .expectState(VALIDATION_PENDING)
                        .and().step()
                        .sendEvent(buildEvent(VALIDATION_PASSED))
                        .expectStateChanged(1)
                        .expectState(VALIDATED)
                        .and().build();

        testPlan.test();

        verify(validateOrderAction, times(1)).execute(argThat(new StateContextArgsMatcher(ORDER_ID)));
        verify(validatedOrderStateAction, times(1)).execute(argThat(new StateContextArgsMatcher(ORDER_ID)));
    }

    @Test
    void shouldBeValidationExceptionWhenSendValidationFailed() throws Exception {
        StateMachine<BeerOrderStatus, BeerOrderEventEnum> stateMachine = stateMachineFactory.getStateMachine();

        StateMachineTestPlan<BeerOrderStatus, BeerOrderEventEnum> testPlan =
                StateMachineTestPlanBuilder.<BeerOrderStatus, BeerOrderEventEnum>builder()
                        .stateMachine(stateMachine)
                        .step()
                        .expectStateMachineStarted(1)
                        .expectState(BeerOrderStatus.NEW)
                        .and().step()
                        .sendEvent(buildEvent(VALIDATED_ORDER))
                        .expectStateChanged(1)
                        .expectState(VALIDATION_PENDING)
                        .and().step()
                        .sendEvent(buildEvent(VALIDATION_FAILED))
                        .expectStateChanged(1)
                        .expectState(VALIDATION_EXCEPTION)
                        .and().build();

        testPlan.test();

        verify(validateOrderAction, times(1)).execute(argThat(new StateContextArgsMatcher(ORDER_ID)));
        verify(validatedOrderStateAction, times(0)).execute(argThat(new StateContextArgsMatcher(ORDER_ID)));
    }

    private Message<BeerOrderEventEnum> buildEvent(BeerOrderEventEnum event) {
        return MessageBuilder.withPayload(event)
                .setHeader(ORDER_ID_HEADER, ORDER_ID.toString())
                .build();
    }

    @RequiredArgsConstructor
    private static class StateContextArgsMatcher implements ArgumentMatcher<StateContext<BeerOrderStatus, BeerOrderEventEnum>> {

        private final UUID orderId;

        @Override
        public boolean matches(StateContext<BeerOrderStatus, BeerOrderEventEnum> context) {
            return UUID.fromString((String) context.getMessageHeader(ORDER_ID_HEADER)).equals(orderId);
        }

    }

}
