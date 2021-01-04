package ohlim.fooda.service;

import javassist.NotFoundException;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.RestaurantDto;
import ohlim.fooda.dto.RestaurantDto.*;
import ohlim.fooda.repository.AccountRepository;
import ohlim.fooda.repository.RestImageRepository;
import ohlim.fooda.repository.RestaurantRepository;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RestaurantService {

    private RestaurantRepository restaurantRepository;
    private AccountRepository accountRepository;
    private RestImageRepository restImageRepository;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository, AccountRepository accountRepository, RestImageRepository restImageRepository) {
        this.restaurantRepository = restaurantRepository;
        this.accountRepository = accountRepository;
        this.restImageRepository = restImageRepository;
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

    public Restaurant addRestaurant(Restaurant restaurant) throws ParseException, NotFoundException, org.locationtech.jts.io.ParseException {
        if(restaurant.getLatitude() == null && restaurant.getLongitude() == null){
            String locationJson = LocationToGPS.getGPSKakaoApiFromLocation(restaurant.getLocation());
            System.out.println(locationJson);
            List<Double> GPS = LocationToGPS.getLatLonFromJsonString(locationJson);
            restaurant.setLatitude(GPS.get(0));
            restaurant.setLongitude(GPS.get(1));
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


//    public ResRestaurantDto<?,?> getMapRestaurants(String username, Double lat, Double lng, Integer page, Integer size){
//        List<Restaurant> restaurants= restaurantRepository.findByUserName(username);
//        List<vectorIdx> vecAndIdxs = new ArrayList<>();
//        for(int i=0 ; i<restaurants.size() ; i++){
//            vecAndIdxs.add(new vectorIdx(Math.pow(restaurants.get(i).getLat() - lat, 2)
//                    + Math.pow(restaurants.get(i).getLon() -lon ,2),i));
//        }
//
//        vecAndIdxs.sort(new Comparator<vectorIdx>() {
//            @Override
//            public int compare(vectorIdx v1, vectorIdx v2) {
//                return v1.vector.compareTo(v2.vector);
//            }
//        });
//
//        List<RestaurantImageUrlInfo> retRestaurants = new ArrayList<>();
//        int startIdx = (page-1)*size;
//        Boolean isEnd = false;
//        for(int i=0 ; i<size ; i++){
//            if(startIdx + i >= restaurants.size()){
//                isEnd = true;
//                break;
//            }
//            int restaurantsIdx = vecAndIdxs.get(startIdx + i).restaurantIdx;
//            Restaurant restaurant = restaurants.get(restaurantsIdx);
//            List<Object> imageUrls = restImageRepository.getFileUrls(restaurant.getId());
//            RestaurantImageUrlInfo restaurantImageUrlInfo = RestaurantImageUrlInfo.builder()
//                    .restaurant(restaurant)
//                    .imageUrls(imageUrls)
//                    .build();
//            retRestaurants.add(restaurantImageUrlInfo);
//        }
//
//        Map<String, Object> meta = new HashMap<>();
//        meta.put("success", true);
//        meta.put("total_count",vecAndIdxs.size());
//        meta.put("msg", "레스토랑 목록입니다.");
//        meta.put("is_end", isEnd);
//        meta.put("page",page);
//
//        ResRestaurantDto<?, ?> resRestaurantDto = ResRestaurantDto.builder()
//                .meta(meta)
//                .documents(retRestaurants)
//                .build();
//        return resRestaurantDto;
//    }
//
//
//
//    public class vectorIdx {
//        public final Double vector;
//        public final Integer restaurantIdx;
//
//        public vectorIdx(Double vector, Integer restaurantIdx) {
//            this.vector = vector;
//            this.restaurantIdx = restaurantIdx;
//        }
//    }

    public List<Restaurant> getRestaurantByFolderId(Long id) {
        return restaurantRepository.findAllByFolderId(id);
    }

    public ResRestaurantDto<?,?> getMapRestaurants(String username, Double lat, Double lng, Integer page, Integer size){
        List<Restaurant> restaurants = restaurantRepository.getRestaurantOrderByDist(lat, lng, username);
        int totalCount = restaurants.size();
        int startIdx = (page-1)*size;
        boolean isEnd = false;
        int endIdx = 0;
        int pageCount = (int) Math.ceil((float)totalCount / size);

        if(pageCount <= 0) {
            // TODO: 식당이 하나도 없을때 처리
        }else if(pageCount < page){
            // TODO: 허용 page 범위 넘어갔다는 오류 추가
        }else if(pageCount == page){
            isEnd = true;
            endIdx = totalCount-1;
        }else{
            endIdx = startIdx + size;
        }
        restaurants = restaurants.subList(startIdx, endIdx);

        List<RestaurantImageUrlInfo> retRestaurants = new ArrayList<>();
        for(Restaurant restaurant : restaurants){
            List<Object> imageUrls = restImageRepository.getFileUrls(restaurant.getId());
            RestaurantImageUrlInfo restaurantImageUrlInfo = RestaurantImageUrlInfo.builder()
                    .restaurant(restaurant)
                    .imageUrls(imageUrls)
                    .build();
            retRestaurants.add(restaurantImageUrlInfo);
        }

        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("total_count",totalCount);
        meta.put("msg", "레스토랑 목록입니다.");
        meta.put("is_end", isEnd);
        meta.put("page",page);

        return ResRestaurantDto.builder()
                .meta(meta)
                .documents(retRestaurants)
                .build();
    }
}
