package guru.springframework.mssc.beer.order.service.service.test.listener;

import guru.cfg.brewery.model.BeerOrderLineDto;
import guru.cfg.brewery.model.messages.AllocateOrderRequest;
import guru.cfg.brewery.model.messages.AllocateOrderResult;
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
public class BeerOrderAllocationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATING_ORDER_QUEUE)
    public void on(Message message) {

        AllocateOrderRequest request = (AllocateOrderRequest) message.getPayload();

        for (BeerOrderLineDto line : request.getBeerOrder().getBeerOrderLines()) {
            line.setQuantityAllocated(line.getOrderQuantity());
        }


        jmsTemplate.convertAndSend(JmsConfig.ALLOCATING_ORDER_RESULT_QUEUE,
                AllocateOrderResult.builder()
                        .beerOrder(request.getBeerOrder())
                        .pendingInventory(false)
                        .error(false)
                        .build());
    }

}
