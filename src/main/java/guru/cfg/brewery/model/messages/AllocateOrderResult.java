package guru.cfg.brewery.model.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import guru.cfg.brewery.model.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AllocateOrderResult implements Message {

    private static final long serialVersionUID = -8015362340606047422L;

    private BeerOrderDto beerOrder;
    private Boolean error = false;
    private Boolean pendingInventory = false;

}
