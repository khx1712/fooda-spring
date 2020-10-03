package ohlim.fooda.service;

import lombok.Getter;
import lombok.Setter;
import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.domain.RestaurantDto;
import ohlim.fooda.repository.AccountRepository;
import ohlim.fooda.repository.RestaurantRepository;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RestaurantService {

    private RestaurantRepository restaurantRepository;
    private AccountRepository accountRepository;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository, AccountRepository accountRepository) {
        this.restaurantRepository = restaurantRepository;
        this.accountRepository = accountRepository;
    }

    public Restaurant getRestaurant(Long id) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
        return restaurant;
    }

    public List<Restaurant> getRestaurantByName(String username, String restaurantName) {
        List<Restaurant> restaurants= restaurantRepository.findByUserNameAndName(username, restaurantName);
        return restaurants;
    }

    public Restaurant addRestaurant(Restaurant restaurant) throws ParseException {
        if(restaurant.getLat() == null && restaurant.getLon() == null){
            String locationJson = LocationToGPS.getGPSKakaoApiFromLocation(restaurant.getLocation());
            System.out.println(locationJson);
            //List<Double> GPS = LocationToGPS.getLatLonFromJsonString(locationJson);
            //restaurant.setLat(GPS.get(0));
            //restaurant.setLon(GPS.get(1));
        }
        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateRestaurant(Long restaurantId, RestaurantDto.RestaurantInfo restaurantInfo) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurant.setRestaurantInfo(restaurantInfo);
        return restaurantRepository.save(restaurant);
    }

    public void deleteRestaurant(String username, Long id) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantRepository.findByUserNameAndId(username, id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
        restaurantRepository.delete(restaurant);
    }

}
