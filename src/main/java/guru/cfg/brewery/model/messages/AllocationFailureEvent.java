package guru.cfg.brewery.model.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationFailureEvent implements Message {

    private static final long serialVersionUID = -366312159701293581L;

    private UUID orderId;

}
