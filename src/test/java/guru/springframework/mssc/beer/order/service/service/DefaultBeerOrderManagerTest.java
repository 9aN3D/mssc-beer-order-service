package guru.springframework.mssc.beer.order.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import guru.springframework.mssc.beer.order.service.domain.BeerOrder;
import guru.springframework.mssc.beer.order.service.domain.BeerOrderLine;
import guru.springframework.mssc.beer.order.service.domain.Customer;
import guru.springframework.mssc.beer.order.service.repository.BeerOrderRepository;
import guru.springframework.mssc.beer.order.service.repository.CustomerRepository;
import guru.springframework.mssc.beer.order.service.service.beer.BeerService;
import guru.springframework.mssc.beer.order.service.service.beer.model.BeerDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.ALLOCATED;
import static guru.springframework.mssc.beer.order.service.domain.BeerOrderStatus.PICKED_UP;
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
    ObjectMapper objectMapper;

    private WireMockServer wireMockServer;
    private Customer customer;
    private BeerDto beerDto;
    private BeerOrder testBeerOrder;

    private final String BEER_UPC = "023112332";

    @BeforeEach
    void setup() {
        wireMockServer = with(wireMockConfig().port(8094));
        wireMockServer.start();

        customerRepository.deleteAll();
        beerOrderRepository.deleteAll();

        customer = customerRepository.save(Customer.builder()
                .customerName("Ala")
                .build());
        beerDto = buildBeerDto(UUID.randomUUID());
        testBeerOrder = createBeerOrder(beerDto.getId());
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
        beerDto = null;
        testBeerOrder = null;

        customerRepository.deleteAll();
        beerOrderRepository.deleteAll();
    }

    @Test
    void testNewToAllocated() throws JsonProcessingException {
        wireMockServer.stubFor(get(BeerService.BEER_UPC_PATH_V1 + beerDto.getUpc())
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder newBeerOrder = beerOrderManager.processNewBeerOrder(testBeerOrder);

        assertNotNull(newBeerOrder);

        await().untilAsserted(() -> {
            BeerOrder beerOrder = beerOrderRepository.findByIdOrThrow(newBeerOrder.getId());

            assertEquals(ALLOCATED, beerOrder.getOrderStatus());
            BeerOrderLine beerOrderLine = beerOrder.getBeerOrderLines().iterator().next();
            assertEquals(beerOrderLine.getOrderQuantity(), beerOrderLine.getQuantityAllocated());
        });
    }

    @Test
    void testNewToPickedUp() throws JsonProcessingException {
        wireMockServer.stubFor(get(BeerService.BEER_UPC_PATH_V1 + beerDto.getUpc())
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder newBeerOrder = beerOrderManager.processNewBeerOrder(testBeerOrder);

        assertNotNull(newBeerOrder);

        await("testNewToPickedUp" + ALLOCATED.name()).untilAsserted(() -> {
            BeerOrder beerOrder = beerOrderRepository.findByIdOrThrow(newBeerOrder.getId());

            assertEquals(ALLOCATED, beerOrder.getOrderStatus());
            BeerOrderLine beerOrderLine = beerOrder.getBeerOrderLines().iterator().next();
            assertEquals(beerOrderLine.getOrderQuantity(), beerOrderLine.getQuantityAllocated());
        });

        beerOrderManager.processPickup(newBeerOrder.getId());

        await("testNewToPickedUp" + PICKED_UP.name()).untilAsserted(() -> {
            BeerOrder beerOrder = beerOrderRepository.findByIdOrThrow(newBeerOrder.getId());

            assertEquals(PICKED_UP, beerOrder.getOrderStatus());
            BeerOrderLine beerOrderLine = beerOrder.getBeerOrderLines().iterator().next();
            assertEquals(beerOrderLine.getOrderQuantity(), beerOrderLine.getQuantityAllocated());
        });
    }

    private BeerDto buildBeerDto(UUID beerId) {
        return BeerDto.builder()
                .id(beerId)
                .upc(BEER_UPC)
                .build();
    }

    private BeerOrder createBeerOrder(UUID beerId) {
        BeerOrder beerOrder = BeerOrder.builder()
                .customer(customer)
                .build();
        Set<BeerOrderLine> orderLines = new HashSet<>();
        orderLines.add(BeerOrderLine.builder()
                .beerId(beerId)
                .orderQuantity(2)
                .beerOrder(beerOrder)
                .upc(BEER_UPC)
                .build());
        beerOrder.setBeerOrderLines(orderLines);
        return beerOrder;
    }

}
