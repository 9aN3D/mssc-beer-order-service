package guru.cfg.brewery.model.messages;

import guru.cfg.brewery.model.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateOrderRequest implements Message {

    private static final long serialVersionUID = 1813094559026437467L;

    private BeerOrderDto beerOrder;

}
