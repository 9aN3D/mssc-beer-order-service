package guru.springframework.mssc.beer.order.service.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.NEW;

@Getter
@Setter
@Entity
@NoArgsConstructor
@ToString(callSuper = true, exclude = "beerOrderLines")
public class BeerOrder extends BaseEntity {

    private String customerRef;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private Set<BeerOrderLine> beerOrderLines = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private BeerOrderStatus orderStatus = NEW;

    private String orderStatusCallbackUrl;

    @Builder
    public BeerOrder(UUID id,
                     Long version,
                     Timestamp createdDate,
                     Timestamp lastModifiedDate,
                     String customerRef,
                     Customer customer,
                     Set<BeerOrderLine> beerOrderLines,
                     BeerOrderStatus orderStatus,
                     String orderStatusCallbackUrl) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerRef = customerRef;
        this.customer = customer;
        this.beerOrderLines = beerOrderLines;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
    }

}
