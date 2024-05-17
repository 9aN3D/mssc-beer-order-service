package guru.springframework.mssc.beer.order.service.events.producers;

import guru.cfg.brewery.model.messages.Message;
import guru.cfg.brewery.model.messages.ValidateOrderRequest;
import guru.springframework.mssc.beer.order.service.config.JmsConfig;

public interface EventProducer {

    void produce(String destination, Message request);

    default void produceToValidateOrderRequestQueue(ValidateOrderRequest request) {
        produce(JmsConfig.VALIDATING_ORDER_REQUEST_QUEUE, request);
    }

}
