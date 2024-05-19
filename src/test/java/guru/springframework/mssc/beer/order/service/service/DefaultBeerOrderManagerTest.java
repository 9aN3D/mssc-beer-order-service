package guru.springframework.mssc.beer.order.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import guru.springframework.mssc.beer.order.service.domain.BeerOrder;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderEventEnum;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderLine;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus;
import guru.springframework.mssc.beer.order.service.domain.Customer;
import guru.springframework.mssc.beer.order.service.repository.BeerOrderRepository;
import guru.springframework.mssc.beer.order.service.repository.CustomerRepository;
import guru.springframework.mssc.beer.order.service.service.beer.BeerService;
import guru.springframework.mssc.beer.order.service.service.beer.model.BeerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.test.context.TestPropertySource;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.ALLOCATED;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestPropertySource(properties = "app.scheduling.enable=false")
@ExtendWith(WireMockExtension.class)
@SpringBootTest
class DefaultBeerOrderManagerTest {

    @Autowired
    BeerOrderManager beerOrderManager;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private StateMachineFactory<BeerOrderStatus, BeerOrderEventEnum> stateMachineFactory;
    ;

    private Customer customer;

    private final UUID BEER_ID = UUID.randomUUID();
    private final String BEER_UPC = "023112332";

    @TestConfiguration
    static class RestTemplateBuilderProvider {

        @Bean(destroyMethod = "stop")
        public WireMockServer wireMockServer() {
            WireMockServer wireMockServer = with(wireMockConfig().port(8094));
            wireMockServer.start();
            return wireMockServer;
        }

    }

    @BeforeEach
    void setup() {
        customer = customerRepository.save(Customer.builder()
                .customerName("Ala")
                .build());
    }

    @Test
    void testNewToAllocated() throws JsonProcessingException {
        BeerDto beerDto = buildBeerDto();

        wireMockServer.stubFor(get(BeerService.BEER_UPC_PATH_V1 + beerDto.getUpc())
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder testBeerOrder = createBeerOrder();

        BeerOrder newBeerOrder = beerOrderManager.processNewBeerOrder(testBeerOrder);

        assertNotNull(newBeerOrder);

        await().untilAsserted(() -> {
            BeerOrder beerOrder = beerOrderRepository.findByIdOrThrow(newBeerOrder.getId());

            assertEquals(ALLOCATED, beerOrder.getOrderStatus());
            BeerOrderLine beerOrderLine = beerOrder.getBeerOrderLines().iterator().next();
            assertEquals(beerOrderLine.getOrderQuantity(), beerOrderLine.getQuantityAllocated());
        });
    }

    private BeerDto buildBeerDto() {
        return BeerDto.builder()
                .id(BEER_ID)
                .upc(BEER_UPC)
                .build();
    }

    private BeerOrder createBeerOrder() {
        BeerOrder beerOrder = BeerOrder.builder()
                .customer(customer)
                .build();
        Set<BeerOrderLine> orderLines = new HashSet<>();
        orderLines.add(BeerOrderLine.builder()
                .beerId(BEER_ID)
                .orderQuantity(2)
                .beerOrder(beerOrder)
                .upc(BEER_UPC)
                .build());
        beerOrder.setBeerOrderLines(orderLines);
        return beerOrder;
    }

}
