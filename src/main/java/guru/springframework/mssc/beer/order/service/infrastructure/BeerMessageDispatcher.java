package guru.springframework.mssc.beer.order.service.infrastructure;

import guru.cfg.brewery.model.messages.Message;
import guru.springframework.mssc.beer.order.service.service.MessageHandler.MessageHandlerMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
class BeerMessageDispatcher implements MessageDispatcher {

    private final Map<Class<? extends Message>, MessageHandlerMethod> routes = new HashMap<>();

    @Override
    public <T extends Message> void registerHandler(Class<T> type, MessageHandlerMethod<T> handlerMethod) {
        routes.put(type, handlerMethod);
    }

    @Override
    public void dispatch(Message message) {
        ofNullable(routes.get(message.getClass()))
                .ifPresentOrElse(
                        router -> router.handle(message),
                        () -> log.error("No handler registered for message " + message.getClass())
                );
    }

}
