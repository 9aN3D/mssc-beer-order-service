package guru.springframework.mssc.beer.order.service.service.customer;

import guru.cfg.brewery.model.CustomerPagedList;
import org.springframework.data.domain.Pageable;

public interface CustomerQueryService {

    CustomerPagedList listCustomers(Pageable pageable);

}
