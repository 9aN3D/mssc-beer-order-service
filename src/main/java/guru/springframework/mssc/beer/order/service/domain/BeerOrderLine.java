package guru.springframework.mssc.beer.order.service.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class BeerOrderLine extends BaseEntity {

    @ManyToOne
    private BeerOrder beerOrder;

    private UUID beerId;

    private Integer orderQuantity = 0;

    private Integer quantityAllocated = 0;

    @Builder
    public BeerOrderLine(UUID id,
                         Long version,
                         Timestamp createdDate,
                         Timestamp lastModifiedDate,
                         BeerOrder beerOrder,
                         UUID beerId,
                         Integer orderQuantity,
                         Integer quantityAllocated) {
        super(id, version, createdDate, lastModifiedDate);
        this.beerOrder = beerOrder;
        this.beerId = beerId;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }

}
