package guru.springframework.mssc.beer.order.service.service;

import guru.cfg.brewery.model.BeerOrderDto;
import guru.cfg.brewery.model.BeerOrderLineDto;
import guru.springframework.mssc.beer.order.service.domain.BeerOrder;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderLine;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import guru.springframework.mssc.beer.order.service.repository.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.ALLOCATION_FAILED;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.ALLOCATION_NO_INVENTORY;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.ALLOCATION_SUCCESS;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.VALIDATED_ORDER;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.VALIDATION_FAILED;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum.VALIDATION_PASSED;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.NEW;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
class DefaultBeerOrderManager implements BeerOrderManager {

    private final StateMachineFactory<BeerOrderStatus, BeerOrderEventEnum> stateMachineFactory;
    private final BeerOrderRepository beerOrderRepository;
    private final StateMachineInterceptorAdapter<BeerOrderStatus, BeerOrderEventEnum> beerOrderStateChangeInterceptor;

    @Override
    public BeerOrder processNewBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(NEW);

        BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);
        sendBeerOrderEvent(savedBeerOrder, VALIDATED_ORDER);
        return savedBeerOrder;
    }

    @Override
    public void processValidationResult(UUID orderId, Boolean isValid) {
        BeerOrder beerOrder = beerOrderRepository.findByIdOrThrow(orderId);
        sendBeerOrderEvent(beerOrder, isValid ? VALIDATION_PASSED : VALIDATION_FAILED);
    }

    @Override
    public void processAllocationFailed(UUID orderId) {
        BeerOrder beerOrder = beerOrderRepository.findByIdOrThrow(orderId);
        sendBeerOrderEvent(beerOrder, ALLOCATION_FAILED);
    }

    @Override
    public void processAllocationPendingInventory(BeerOrderDto beerOrderDto) {
        BeerOrder beerOrder = beerOrderRepository.findByIdOrThrow(beerOrderDto.getId());
        sendBeerOrderEvent(beerOrder, ALLOCATION_NO_INVENTORY);
        updateAllocatedQtyWithRetryPolicy(beerOrderDto);
    }

    @Override
    public void processAllocationPassed(BeerOrderDto beerOrderDto) {
        BeerOrder beerOrder = beerOrderRepository.findByIdOrThrow(beerOrderDto.getId());
        sendBeerOrderEvent(beerOrder, ALLOCATION_SUCCESS);
        updateAllocatedQtyWithRetryPolicy(beerOrderDto);
    }

    private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEventEnum eventEnum) {
        build(beerOrder)
                .sendEvent(MessageBuilder.withPayload(eventEnum)
                        .setHeader(ORDER_ID_HEADER, beerOrder.getId().toString())
                        .build());
    }

    private StateMachine<BeerOrderStatus, BeerOrderEventEnum> build(BeerOrder beerOrder) {
        StateMachine<BeerOrderStatus, BeerOrderEventEnum> stateMachine = stateMachineFactory.getStateMachine(beerOrder.getId());

        stateMachine.stop();

        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(beerOrderStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null, null));
                });
        stateMachine.start();
        return stateMachine;
    }

    private void updateAllocatedQtyWithRetryPolicy(BeerOrderDto beerOrderDto) {
        Failsafe.with(new RetryPolicy<>()
                        .handle(Exception.class)
                        .withMaxAttempts(1)
                        .withDelay(Duration.ofSeconds(2))
                        .onRetry(e -> log.info("Update allocated qty: {attempt: {}, orderId: {}}", e.getAttemptCount(), beerOrderDto.getId())))
                .run(() -> updateAllocatedQty(beerOrderDto));
    }

    private void updateAllocatedQty(BeerOrderDto beerOrderDto) {
        BeerOrder beerOrder = beerOrderRepository.findByIdOrThrow(beerOrderDto.getId());
        Map<UUID, BeerOrderLineDto> beerOrderLineIdToBeerOrderLineDto = beerOrderDto.collectBeerOrderLineByBeerOrderLineId();

        for (BeerOrderLine line : beerOrder.getBeerOrderLines()) {
            beerOrderLineIdToBeerOrderLineDto.computeIfPresent(line.getId(), (id, dto) -> {
                line.setQuantityAllocated(dto.getQuantityAllocated());
                return dto;
            });
        }
        beerOrderRepository.saveAndFlush(beerOrder);
    }

}
