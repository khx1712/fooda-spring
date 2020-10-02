package ohlim.fooda.service;

import lombok.Getter;
import lombok.Setter;
import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.domain.RestaurantDto;
import ohlim.fooda.repository.AccountRepository;
import ohlim.fooda.repository.RestaurantRepository;
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

    public RestaurantDto.ResRestaurantVO<?,?> getMapRestaurants(String username, Double lat, Double lon, Integer page, Integer size){
        List<Restaurant> restaurants= restaurantRepository.findByUserName(username);
        List<Pair<Double,Integer>> vecAndIdx = new ArrayList<>();
        for(int i=0 ; i<restaurants.size() ; i++){
            vecAndIdx.add(new Pair<>(Math.pow(restaurants.get(i).getLat() - lat,2)
                    + Math.pow(restaurants.get(i).getLon() -lon ,2),i));
        }

        vecAndIdx.sort(new Comparator<Pair<Double, Integer>>() {
            @Override
            public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2) {
                if(o1.key > o2.key)
                    return 1;
                else return 0;
            }
        });

        List<Restaurant> retRestaurants = new ArrayList<>();
        int startIdx = (page-1)*size;
        Boolean isEnd = false;
        for(int i=0 ; i<size ; i++){
            if(startIdx + i <= restaurants.size()){
                isEnd = true;
                break;
            }
            int restaurantsIdx = vecAndIdx.get(startIdx + i).value;
            retRestaurants.add(restaurants.get(restaurantsIdx));
        }

        //return retRestaurant;
        RestaurantDto.MapRestaurantsMeta mapRestaurantsMeta = RestaurantDto.MapRestaurantsMeta.builder()
                .is_end(isEnd).page(page).build();

        RestaurantDto.ResRestaurantVO<?, ?> resRestaurantVO = RestaurantDto.ResRestaurantVO.builder()
                .meta(mapRestaurantsMeta)
                .documents(retRestaurants)
                .build();
        return resRestaurantVO;
    }

    public List<Restaurant> getRestaurantByName(String username, String restaurantName) {
        List<Restaurant> restaurants= restaurantRepository.findByUserNameAndName(username, restaurantName);
        return restaurants;
    }

    public Restaurant addRestaurant(Restaurant restaurant) {
        Restaurant saved = restaurantRepository.save(restaurant);
        return saved;
    }

    public class Pair<K, V> {

        public final K key;
        public final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public boolean equals(Object o) {
            return o instanceof Pair && Objects.equals(key, ((Pair<?,?>)o).key) && Objects.equals(value, ((Pair<?,?>)o).value);
        }

        public int hashCode() {
            return 31 * Objects.hashCode(key) + Objects.hashCode(value);
        }

        public String toString() {
            return key + "=" + value;
        }
    }
}
