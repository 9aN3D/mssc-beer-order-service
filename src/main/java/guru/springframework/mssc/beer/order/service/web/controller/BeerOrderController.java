package guru.springframework.mssc.beer.order.service.web.controller;

import guru.springframework.mssc.beer.order.service.service.BeerOrderFacade;
import guru.springframework.mssc.beer.order.service.service.BeerOrderService;
import guru.cfg.brewery.model.BeerOrderDto;
import guru.cfg.brewery.model.BeerOrderPagedList;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/customers/{customerId}")
public class BeerOrderController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderFacade beerOrderFacade;

    @GetMapping("/orders")
    @ResponseStatus(OK)
    public BeerOrderPagedList listOrders(@PathVariable("customerId") UUID customerId,
                                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        return beerOrderFacade.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @PostMapping("/orders")
    @ResponseStatus(CREATED)
    public BeerOrderDto placeOrder(@PathVariable("customerId") UUID customerId,
                                   @RequestBody BeerOrderDto beerOrderDto) {
        return beerOrderFacade.placeOrder(customerId, beerOrderDto);
    }

    @GetMapping("/orders/{orderId}")
    @ResponseStatus(OK)
    public BeerOrderDto getOrder(@PathVariable("customerId") UUID customerId,
                                 @PathVariable("orderId") UUID orderId) {
        return beerOrderFacade.getOrderById(customerId, orderId);
    }

    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(NO_CONTENT)
    public void pickupOrder(@PathVariable("customerId") UUID customerId,
                            @PathVariable("orderId") UUID orderId) {
        beerOrderFacade.pickupOrder(customerId, orderId);
    }

}
