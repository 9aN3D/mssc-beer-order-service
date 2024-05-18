package guru.cfg.brewery.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.function.Function.identity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "beerOrderLines")
public class BeerOrderDto {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("version")
    private Integer version;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    @JsonProperty("createdDate")
    private OffsetDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    @JsonProperty("lastModifiedDate")
    private OffsetDateTime lastModifiedDate;

    private UUID customerId;

    private String customerRef;

    private List<BeerOrderLineDto> beerOrderLines;

    private String orderStatus;

    private String orderStatusCallbackUrl;

    @JsonIgnore
    public Map<UUID, BeerOrderLineDto> collectBeerOrderLineByBeerOrderLineId() {
        if (isNull(beerOrderLines)) {
            return Collections.emptyMap();
        }
        return beerOrderLines.stream()
                .filter(value -> nonNull(value.getId()))
                .collect(Collectors.toMap(BeerOrderLineDto::getId, identity()));
    }

}
