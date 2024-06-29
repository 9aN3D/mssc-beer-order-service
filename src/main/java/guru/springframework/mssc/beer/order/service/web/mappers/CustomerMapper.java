package guru.springframework.mssc.beer.order.service.web.mappers;

import guru.cfg.brewery.model.CustomerDto;
import guru.springframework.mssc.beer.order.service.domain.Customer;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface CustomerMapper {

    CustomerDto customerToDto(Customer customer);

    Customer dtoToCustomer(CustomerDto dto);

}
