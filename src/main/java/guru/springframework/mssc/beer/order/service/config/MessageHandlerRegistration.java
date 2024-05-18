package guru.springframework.mssc.beer.order.service.config;

import guru.cfg.brewery.model.messages.AllocateOrderResult;
import guru.cfg.brewery.model.messages.AllocatedOrderEvent;
import guru.cfg.brewery.model.messages.ValidateOrderResult;
import guru.springframework.mssc.beer.order.service.infrastructure.MessageDispatcher;
import guru.springframework.mssc.beer.order.service.service.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MessageHandlerRegistration {

    private final MessageDispatcher messageDispatcher;
    private final MessageHandler messageHandler;

    @PostConstruct
    public void init() {
        messageDispatcher.registerHandler(ValidateOrderResult.class, messageHandler::handle);
        messageDispatcher.registerHandler(AllocateOrderResult.class, messageHandler::handle);
        messageDispatcher.registerHandler(AllocatedOrderEvent.class, messageHandler::handle);

        log.info("Registered message handlers");
    }

}
