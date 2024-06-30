package guru.springframework.mssc.beer.order.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsscBeerOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsscBeerOrderServiceApplication.class, args);
    }

}
