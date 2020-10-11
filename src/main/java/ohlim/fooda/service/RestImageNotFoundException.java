package ohlim.fooda.service;

public class RestImageNotFoundException extends Throwable {
    RestImageNotFoundException(Long id){
        super("RestImage not found: " + id);
    }
}
