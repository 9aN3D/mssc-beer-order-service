package guru.cfg.brewery.model.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllocatedOrderEvent implements Message {

    private static final long serialVersionUID = 426566288411435415L;

    private UUID beerOrderId;

}
