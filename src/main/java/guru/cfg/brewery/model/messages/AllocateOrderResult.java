package guru.cfg.brewery.model.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import guru.cfg.brewery.model.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.lang.Boolean.TRUE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateOrderResult implements Message {

    private static final long serialVersionUID = -7899258823593909559L;

    private BeerOrderDto beerOrder;
    private Boolean error;
    private Boolean pendingInventory;

    @JsonIgnore
    public boolean isError() {
        return TRUE.equals(error);
    }

    @JsonIgnore
    public boolean isPendingInventory() {
        return TRUE.equals(pendingInventory);
    }

    @JsonIgnore
    public boolean isSuccessful() {
        return !isError();
    }

}
