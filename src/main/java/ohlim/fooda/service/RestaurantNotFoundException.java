package ohlim.fooda.service;

public class RestaurantNotFoundException extends Throwable {

    RestaurantNotFoundException(Long id){
        super("Restaurant not found: " + id);
    }
}
