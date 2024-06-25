package guru.springframework.mssc.beer.order.service.events.producers;

import guru.cfg.brewery.model.messages.AllocateOrderRequest;
import guru.cfg.brewery.model.messages.AllocationFailureEvent;
import guru.cfg.brewery.model.messages.Message;
import guru.cfg.brewery.model.messages.ValidateOrderRequest;

import static guru.springframework.mssc.beer.order.service.config.JmsConfig.ALLOCATING_FAILURE_QUEUE;
import static guru.springframework.mssc.beer.order.service.config.JmsConfig.ALLOCATING_ORDER_QUEUE;
import static guru.springframework.mssc.beer.order.service.config.JmsConfig.VALIDATING_ORDER_REQUEST_QUEUE;

public interface EventProducer {

    void produce(String destination, Message request);

    default void produceToValidateOrderRequestQueue(ValidateOrderRequest request) {
        produce(VALIDATING_ORDER_REQUEST_QUEUE, request);
    }

    default void produceToAllocateOrderQueue(AllocateOrderRequest request) {
        produce(ALLOCATING_ORDER_QUEUE, request);
    }

    default void produceToAllocatingFailureQueue(AllocationFailureEvent event) {
        produce(ALLOCATING_FAILURE_QUEUE, event);
    }

}
