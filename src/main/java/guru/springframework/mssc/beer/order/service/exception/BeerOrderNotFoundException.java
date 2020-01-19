package guru.springframework.mssc.beer.order.service.exception;

public class BeerOrderNotFoundException extends RuntimeException {

    public BeerOrderNotFoundException(String message) {
        super(message);
    }

}
