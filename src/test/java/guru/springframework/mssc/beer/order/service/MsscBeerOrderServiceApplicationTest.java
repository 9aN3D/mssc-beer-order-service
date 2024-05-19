package guru.springframework.mssc.beer.order.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "app.scheduling.enable=false")
@SpringBootTest
class MsscBeerOrderServiceApplicationTest {

    @Test
    void contextLoads() {
    }

}
