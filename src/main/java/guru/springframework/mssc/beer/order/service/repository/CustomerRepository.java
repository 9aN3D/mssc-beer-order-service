package guru.springframework.mssc.beer.order.service.repository;

import guru.springframework.mssc.beer.order.service.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    List<Customer> findAllByCustomerNameLike(String customerName);

}
