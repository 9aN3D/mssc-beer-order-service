package guru.springframework.mssc.beer.order.service.service.listeners;

import guru.cfg.brewery.model.messages.AllocateOrderResult;
import guru.cfg.brewery.model.messages.AllocatedOrderEvent;
import guru.cfg.brewery.model.messages.ValidateOrderResult;
import guru.springframework.mssc.beer.order.service.config.JmsConfig;
import guru.springframework.mssc.beer.order.service.infrastructure.MessageDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JmsExternalMessageListener {

    private final MessageDispatcher messageDispatcher;

    @JmsListener(destination = JmsConfig.VALIDATING_ORDER_RESULT_QUEUE)
    public void on(ValidateOrderResult result) {
        log.debug("Receiving ValidateOrderResult: {}", result);

        messageDispatcher.dispatch(result);
    }

    @JmsListener(destination = JmsConfig.ALLOCATING_ORDER_RESULT_QUEUE)
    public void on(AllocateOrderResult result) {
        log.debug("Receiving AllocateOrderResult: {}", result);

        messageDispatcher.dispatch(result);
    }

    @JmsListener(destination = JmsConfig.ALLOCATED_ORDER_QUEUE)
    public void on(AllocatedOrderEvent event) {
        log.debug("Receiving AllocatedOrderEvent: {}", event);

        messageDispatcher.dispatch(event);
    }

}
