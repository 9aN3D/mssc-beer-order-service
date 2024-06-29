package guru.springframework.mssc.beer.order.service.web.controller;

import guru.cfg.brewery.model.CustomerPagedList;
import guru.springframework.mssc.beer.order.service.service.customer.CustomerQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final CustomerQueryService customerQueryService;

    @GetMapping
    @ResponseStatus(OK)
    public CustomerPagedList listCustomers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                           @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        return customerQueryService.listCustomers(PageRequest.of(pageNumber, pageSize));
    }

}
