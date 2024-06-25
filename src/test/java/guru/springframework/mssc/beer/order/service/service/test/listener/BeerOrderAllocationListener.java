package guru.springframework.mssc.beer.order.service.service.test.listener;

import guru.cfg.brewery.model.BeerOrderLineDto;
import guru.cfg.brewery.model.messages.AllocateOrderRequest;
import guru.cfg.brewery.model.messages.AllocateOrderResult;
import guru.springframework.mssc.beer.order.service.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BeerOrderAllocationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATING_ORDER_QUEUE)
    public void on(Message message) {

        AllocateOrderRequest request = (AllocateOrderRequest) message.getPayload();
        String customerRef = request.getBeerOrder().getCustomerRef();

        if (!"dont-allocate".equals(customerRef)) {
            boolean hasError = "fail-allocation".equals(customerRef);
            boolean pendingInventory = "partial-allocation".equals(customerRef);

            if (!hasError) {
                for (BeerOrderLineDto line : request.getBeerOrder().getBeerOrderLines()) {
                    line.setQuantityAllocated(pendingInventory ? line.getOrderQuantity() - 1 : line.getOrderQuantity());
                }
            }

            jmsTemplate.convertAndSend(JmsConfig.ALLOCATING_ORDER_RESULT_QUEUE,
                    AllocateOrderResult.builder()
                            .beerOrder(request.getBeerOrder())
                            .pendingInventory(pendingInventory)
                            .error(hasError)
                            .build());
        }
    }

}
