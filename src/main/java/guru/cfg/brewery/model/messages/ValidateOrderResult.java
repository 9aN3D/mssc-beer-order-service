package guru.cfg.brewery.model.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateOrderResult implements Message {

    private static final long serialVersionUID = 79077083668084799L;

    private UUID orderId;
    private Boolean isValid;

}
