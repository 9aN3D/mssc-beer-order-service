package guru.springframework.mssc.beer.order.service.service.customer;

import guru.cfg.brewery.model.CustomerPagedList;
import guru.springframework.mssc.beer.order.service.repository.CustomerRepository;
import guru.springframework.mssc.beer.order.service.web.mappers.CustomerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class RepositoryCustomerQueryService implements CustomerQueryService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerPagedList listCustomers(Pageable pageable) {
        log.trace("Getting list customers {pageable: {}}", pageable);

        var page = customerRepository.findAll(pageable)
                .map(customerMapper::customerToDto);

        log.info("Got list customers {pageable: {}, total elements: {}}", pageable, page.getTotalElements());
        return new CustomerPagedList(
                page.getContent(),
                buildPageRequest(page.getPageable()),
                page.getTotalElements());
    }

    private Pageable buildPageRequest(Pageable pageable) {
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }

}
