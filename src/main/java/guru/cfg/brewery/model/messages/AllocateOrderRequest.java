package guru.cfg.brewery.model.messages;

import guru.cfg.brewery.model.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllocateOrderRequest implements Message {

    private static final long serialVersionUID = -5254796769430478706L;

    private BeerOrderDto beerOrder;

}
