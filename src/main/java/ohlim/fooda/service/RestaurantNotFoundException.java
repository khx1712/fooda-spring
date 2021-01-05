package ohlim.fooda.service;

public class RestaurantNotFoundException extends Throwable {
    RestaurantNotFoundException(String msg){
        super(msg);
    }
}
