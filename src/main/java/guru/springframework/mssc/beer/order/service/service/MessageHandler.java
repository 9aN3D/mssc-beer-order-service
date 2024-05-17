package guru.springframework.mssc.beer.order.service.service;

import guru.cfg.brewery.model.messages.Message;
import guru.cfg.brewery.model.messages.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public interface MessageHandler {

    void handle(ValidateOrderResult result);

    interface MessageHandlerMethod<T extends Message> {

        void handle(T event);

    }

    @Service
    @RequiredArgsConstructor
    class DefaultMessageHandler implements MessageHandler {

        private final BeerOrderManager beerOrderManager;

        @Override
        public void handle(ValidateOrderResult result) {
            beerOrderManager.processValidationResult(result.getOrderId(), result.getIsValid());
        }

    }

}
