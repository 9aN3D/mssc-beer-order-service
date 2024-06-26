package guru.springframework.mssc.beer.order.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {

    public static final String VALIDATING_ORDER_REQUEST_QUEUE = "validate-order";
    public static final String VALIDATING_ORDER_RESULT_QUEUE = "validate-order-result";
    public static final String ALLOCATING_ORDER_QUEUE = "allocate-order";
    public static final String ALLOCATING_ORDER_RESULT_QUEUE = "allocate-order-result";
    public static final String ALLOCATING_FAILURE_QUEUE = "allocation-failure";
    public static final String DEALLOCATING_ORDER_QUEUE = "deallocate-order";

    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(objectMapper);
        return converter;
    }

}
