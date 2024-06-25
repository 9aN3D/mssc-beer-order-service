package guru.springframework.mssc.beer.order.service.service.test.listener;

import guru.cfg.brewery.model.messages.ValidateOrderRequest;
import guru.cfg.brewery.model.messages.ValidateOrderResult;
import guru.springframework.mssc.beer.order.service.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Duration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import static org.awaitility.Awaitility.await;

@Slf4j
@Component
@RequiredArgsConstructor
public class BeerOrderValidationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATING_ORDER_REQUEST_QUEUE)
    public void on(Message message) {

        ValidateOrderRequest request = (ValidateOrderRequest) message.getPayload();

        boolean isValid = !"fail-validation".equals(request.getBeerOrder().getCustomerRef());

        jmsTemplate.convertAndSend(JmsConfig.VALIDATING_ORDER_RESULT_QUEUE,
                ValidateOrderResult.builder()
                        .isValid(isValid)
                        .orderId(request.getBeerOrder().getId())
                        .build());
    }

}
