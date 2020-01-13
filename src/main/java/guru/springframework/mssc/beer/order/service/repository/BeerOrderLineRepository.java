package guru.springframework.mssc.beer.order.service.repository;

import guru.springframework.mssc.beer.order.service.domain.BeerOrderLine;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BeerOrderLineRepository extends PagingAndSortingRepository<BeerOrderLine, UUID> {

}
