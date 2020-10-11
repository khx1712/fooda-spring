package ohlim.fooda.controller;

import javassist.NotFoundException;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.RestaurantDto.*;
import ohlim.fooda.service.RestaurantNotFoundException;
import ohlim.fooda.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RestaurantController {

    private Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);
    @Autowired
    RestaurantService restaurantService;

    @PostMapping("/user/restaurant")
    public ResponseEntity<?> create(
            Authentication authentication,
            @RequestBody RestaurantInfo resource) throws URISyntaxException, ParseException, NotFoundException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantInfo(resource);
        restaurant.setUserName(userDetails.getUsername());
        Restaurant saved = restaurantService.addRestaurant(restaurant);
        System.out.println("create restaurant : " + saved.getId());
        URI location_uri = new URI("/user/restaurant/" + saved.getId());
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "레스토랑을 추가하였습니다.");
        meta.put("restaurantId", saved.getId());
        ResRestaurantDto<?,?> resRestaurantDto = ResRestaurantDto.builder()
                .meta(meta)
                .build();
        return ResponseEntity.created(location_uri).body(resRestaurantDto);
    }


    @PatchMapping("/user/restaurant/{restaurantId}")
    public ResponseEntity<?> update(
            @PathVariable("restaurantId") Long id,
            @RequestBody RestaurantInfo resource) throws URISyntaxException, RestaurantNotFoundException {
        Restaurant updated = restaurantService.updateRestaurant(id, resource);
        System.out.println("create restaurant : " + updated.getId());
        URI location_uri = new URI("/user/restaurant/" + updated.getId());
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "레스토랑을 수정하였습니다.");
        meta.put("restaurantId", updated.getId());
        ResRestaurantDto<?,?> resRestaurantDto = ResRestaurantDto.builder()
                .meta(meta)
                .build();
        return ResponseEntity.created(location_uri).body(resRestaurantDto);
    }

    @DeleteMapping("/user/restaurant/{restaurantId}")
    public ResponseEntity<?> delete(
            Authentication authentication,
            @PathVariable("restaurantId") Long id
    ) throws RestaurantNotFoundException, URISyntaxException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        restaurantService.deleteRestaurant(userDetails.getUsername(), id);
        URI location_uri = new URI("/user/restaurant/" + id);
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "레스토랑을 삭제하였습니다.");
        meta.put("restaurantId", id);
        ResRestaurantDto<?,?> resRestaurantDto = ResRestaurantDto.builder()
                .meta(meta)
                .build();
        return ResponseEntity.created(location_uri).body(resRestaurantDto);
    }

    @GetMapping("/user/restaurant/{restaurantId}")
    public ResponseEntity<?> detail(@PathVariable("restaurantId") Long id) throws RestaurantNotFoundException, URISyntaxException {
        Restaurant restaurant = restaurantService.getRestaurant(id);
        URI location_uri = new URI("/user/restaurant/" + id);
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "레스토랑의 상세입니다.");
        meta.put("restaurantId", restaurant.getId());
        ResRestaurantDto<?,?> resRestaurantDto = ResRestaurantDto.builder()
                .meta(meta)
                .documents(restaurant)
                .build();
        return ResponseEntity.created(location_uri).body(resRestaurantDto);
    }

    @GetMapping("/user/restaurants")
    public ResponseEntity<?> detailByName(
            Authentication authentication,
            @RequestParam("name") String name
    ) throws RestaurantNotFoundException, URISyntaxException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<Restaurant> restaurants = restaurantService.getRestaurantByName(userDetails.getUsername(), name);
        URI location_uri = new URI("/user/restaurants");
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "레스토랑의 목록입니다.");
        meta.put("restaurantCnt", restaurants.size());
        ResRestaurantDto<?,?> resRestaurantDto = ResRestaurantDto.builder()
                .meta(meta)
                .documents(restaurants)
                .build();
        return ResponseEntity.created(location_uri).body(resRestaurantDto);
    }

    @GetMapping("/user/map/restaurants")
    public ResRestaurantDto<?,?> list(
            Authentication authentication,
            @RequestParam("lat") Double lat,
            @RequestParam("lon") Double lon,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return restaurantService.getMapRestaurants(userDetails.getUsername(),lat, lon, page, size);
    }
}
