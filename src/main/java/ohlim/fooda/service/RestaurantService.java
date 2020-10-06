package ohlim.fooda.service;

import javassist.NotFoundException;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.RestaurantDto.*;
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

    public Restaurant addRestaurant(Restaurant restaurant) throws ParseException, NotFoundException {
        if(restaurant.getLat() == null && restaurant.getLon() == null){
            String locationJson = LocationToGPS.getGPSKakaoApiFromLocation(restaurant.getLocation());
            System.out.println(locationJson);
            List<Double> GPS = LocationToGPS.getLatLonFromJsonString(locationJson);
            restaurant.setLat(GPS.get(0));
            restaurant.setLon(GPS.get(1));
        }
        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateRestaurant(Long restaurantId, RestaurantInfo restaurantInfo) throws RestaurantNotFoundException {
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


    public ResRestaurantDto<?,?> getMapRestaurants(String username, Double lat, Double lon, Integer page, Integer size){
        List<Restaurant> restaurants= restaurantRepository.findByUserName(username);
        List<vectorIdx> vecAndIdxs = new ArrayList<>();
        for(int i=0 ; i<restaurants.size() ; i++){
            vecAndIdxs.add(new vectorIdx(Math.pow(restaurants.get(i).getLat() - lat, 2)
                    + Math.pow(restaurants.get(i).getLon() -lon ,2),i));
        }

        vecAndIdxs.sort(new Comparator<vectorIdx>() {
            @Override
            public int compare(vectorIdx v1, vectorIdx v2) {
                return v1.vector.compareTo(v2.vector);
            }
        });

        List<Restaurant> retRestaurants = new ArrayList<>();
        int startIdx = (page-1)*size;
        Boolean isEnd = false;
        for(int i=0 ; i<size ; i++){
            if(startIdx + i >= restaurants.size()){
                isEnd = true;
                break;
            }
            int restaurantsIdx = vecAndIdxs.get(startIdx + i).restaurantIdx;
            retRestaurants.add(restaurants.get(restaurantsIdx));
        }

        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("total_count",vecAndIdxs.size());
        meta.put("msg", "레스토랑 목록입니다.");
        meta.put("is_end", isEnd);
        meta.put("page",page);

        ResRestaurantDto<?, ?> resRestaurantDto = ResRestaurantDto.builder()
                .meta(meta)
                .documents(retRestaurants)
                .build();
        return resRestaurantDto;
    }

    public List<Restaurant> getRestaurantByFolderId(Long id) {
        return restaurantRepository.findAllByFolderId(id);
    }


    public class vectorIdx {
        public final Double vector;
        public final Integer restaurantIdx;

        public vectorIdx(Double vector, Integer restaurantIdx) {
            this.vector = vector;
            this.restaurantIdx = restaurantIdx;
        }
    }
}
