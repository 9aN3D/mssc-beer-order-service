package guru.springframework.mssc.beer.order.service.exception;

public class OrderNotForCustomerException extends RuntimeException {

    public OrderNotForCustomerException(String message) {
        super(message);
    }
}
