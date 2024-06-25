package guru.springframework.mssc.beer.order.service.repository;

import guru.springframework.mssc.beer.order.service.domain.BeerOrder;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import guru.springframework.mssc.beer.order.service.domain.Customer;
import guru.springframework.mssc.beer.order.service.exception.BeerOrderNotFoundException;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.lang.String.format;

@Repository
public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {

    Page<BeerOrder> findAllByCustomer(Customer customer, Pageable pageable);

    List<BeerOrder> findAllByOrderStatus(BeerOrderStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    BeerOrder findOneById(UUID id);

/*    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BeerOrder> findById(UUID orderId);*/

    @Transactional
    default BeerOrder findByIdOrThrow(UUID orderId) {
        return findById(orderId)
                .orElseThrow(() -> new BeerOrderNotFoundException(format("Beer order not found: %s", orderId)));
    }

    @Transactional
    default BeerOrder getOrderWithRetryPolicy(UUID orderId, BeerOrderStatus status, int maxAttempts, Duration delay, Logger log) {
        return Failsafe.with(new RetryPolicy<BeerOrder>()
                        .handle(BeerOrderNotFoundException.class)
                        .handleResultIf(value -> value.getOrderStatus() != status)
                        .withMaxAttempts(maxAttempts)
                        .withDelay(delay)
                        .onRetry(e -> log.info("Getting order: {attempt: {}, orderId: {}, status: {}}", e.getAttemptCount(), orderId, status)))
                .get(() -> findByIdOrThrow(orderId));
    }

    @Transactional
    default BeerOrder getOrderWithRetryPolicy(UUID orderId, Set<BeerOrderStatus> statuses, int maxAttempts, Duration delay, Logger log) {
        return Failsafe.with(new RetryPolicy<BeerOrder>()
                        .handle(BeerOrderNotFoundException.class)
                        .handleResultIf(value -> !statuses.contains(value.getOrderStatus()))
                        .withMaxAttempts(maxAttempts)
                        .withDelay(delay)
                        .onRetry(e -> log.info("Getting order: {attempt: {}, orderId: {}, statuses: {}}", e.getAttemptCount(), orderId, statuses)))
                .get(() -> findByIdOrThrow(orderId));
    }

}
