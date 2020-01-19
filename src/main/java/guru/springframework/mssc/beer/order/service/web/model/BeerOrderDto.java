package guru.springframework.mssc.beer.order.service.web.model;

import guru.springframework.mssc.beer.order.service.domain.OrderStatusEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "beerOrderLines")
public class BeerOrderDto extends BaseItem {

    private UUID customerId;

    private String customerRef;

    private List<BeerOrderLineDto> beerOrderLines;

    private OrderStatusEnum orderStatus;

    private String orderStatusCallbackUrl;

    @Builder
    public BeerOrderDto(UUID id,
                        Integer version,
                        OffsetDateTime createdDate,
                        OffsetDateTime lastModifiedDate,
                        UUID customerId,
                        List<BeerOrderLineDto> beerOrderLines,
                        OrderStatusEnum orderStatus,
                        String orderStatusCallbackUrl,
                        String customerRef) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerId = customerId;
        this.beerOrderLines = beerOrderLines;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
        this.customerRef = customerRef;
    }

}
