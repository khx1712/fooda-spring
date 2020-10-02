package ohlim.fooda.controller;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.domain.RestaurantDto;
import ohlim.fooda.service.RestaurantNotFoundException;
import ohlim.fooda.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
            @RequestBody RestaurantDto.RegistRestaurantInfo resource) throws URISyntaxException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long folderId = resource.getFolderId();
        String userName = userDetails.getUsername();
        String name = resource.getName();
        String phoneNumber = resource.getPhoneNumber();
        Double lat = resource.getLat();
        Double lon = resource.getLon();
        String location = resource.getLocation();
        Character category = resource.getCategory();
        String businessHour = resource.getBusinessHour();
        Restaurant restaurant = Restaurant.builder()
                .folderId(folderId)
                .userName(userName)
                .name(name)
                .phoneNumber(phoneNumber)
                .lat(lat)
                .lon(lon)
                .location(location)
                .category(category)
                .businessHour(businessHour)
                .build();
        Restaurant saved = restaurantService.addRestaurant(restaurant);
        System.out.println("create restaurant : " + saved.getId());
        URI location_uri = new URI("/user/restaurant/" + saved.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("restaurantId", saved.getId());
        return ResponseEntity.created(location_uri).body(map);
    }


    @GetMapping("/user/map/restaurants")
    public RestaurantDto.ResRestaurantVO<?,?> list(
            Authentication authentication,
            @RequestParam("lat") Double lat,
            @RequestParam("lon") Double lon,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        RestaurantDto.ResRestaurantVO<RestaurantDto.MapRestaurantsMeta, List<Restaurant>> resRestaurantVO
                = (RestaurantDto.ResRestaurantVO<RestaurantDto.MapRestaurantsMeta, List<Restaurant>>)
                restaurantService.getMapRestaurants(userDetails.getUsername(),lat, lon, page, size );
        return resRestaurantVO;
    }

    @GetMapping("/user/restaurant/{restaurantId}")
    public Restaurant detail(@PathVariable("restaurantId") Long id) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantService.getRestaurant(id);
        return restaurant;
    }

    @GetMapping("/user/restaurants")
    public List<Restaurant> detailByName(
            Authentication authentication,
            @RequestParam("name") String name
    ) throws RestaurantNotFoundException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<Restaurant> restaurants = restaurantService.getRestaurantByName(userDetails.getUsername(), name);
        return restaurants;
    }
}
