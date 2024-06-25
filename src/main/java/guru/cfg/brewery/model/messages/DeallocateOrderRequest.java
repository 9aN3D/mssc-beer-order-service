package guru.cfg.brewery.model.messages;

import guru.cfg.brewery.model.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeallocateOrderRequest implements Message {

    private static final long serialVersionUID = 6170301393900887819L;

    private BeerOrderDto beerOrder;

}
