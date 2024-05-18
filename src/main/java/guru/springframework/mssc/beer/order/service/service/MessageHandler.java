package guru.springframework.mssc.beer.order.service.service;

import guru.cfg.brewery.model.BeerOrderDto;
import guru.cfg.brewery.model.messages.AllocateOrderResult;
import guru.cfg.brewery.model.messages.Message;
import guru.cfg.brewery.model.messages.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

public interface MessageHandler {

    void handle(ValidateOrderResult result);

    void handle(AllocateOrderResult result);

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

        @Override
        public void handle(AllocateOrderResult result) {
            BeerOrderDto beerOrder = requireNonNull(result.getBeerOrder());
            if (result.isError()) {
                beerOrderManager.processAllocationFailed(beerOrder.getId());
            }
            if (result.isSuccessful() && result.isPendingInventory()) {
                beerOrderManager.processAllocationPendingInventory(result.getBeerOrder());
            }
            if (result.isSuccessful() && !result.isPendingInventory()) {
                beerOrderManager.processAllocationPassed(result.getBeerOrder());
            }
        }

    }

}
