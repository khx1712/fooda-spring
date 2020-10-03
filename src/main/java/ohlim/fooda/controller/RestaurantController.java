package ohlim.fooda.controller;

import io.jsonwebtoken.Claims;
import org.json.simple.parser.ParseException;
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
            @RequestBody RestaurantDto.RestaurantInfo resource) throws URISyntaxException, ParseException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantInfo(resource);
        restaurant.setUserName(userDetails.getUsername());
        Restaurant saved = restaurantService.addRestaurant(restaurant);
        System.out.println("create restaurant : " + saved.getId());
        URI location_uri = new URI("/user/restaurant/" + saved.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("restaurantId", saved.getId());
        return ResponseEntity.created(location_uri).body(map);
    }


    @PatchMapping("/user/restaurant/{restaurantId}")
    public ResponseEntity<?> update(
            @PathVariable("restaurantId") Long id,
            @RequestBody RestaurantDto.RestaurantInfo resource) throws URISyntaxException, RestaurantNotFoundException {
        Restaurant updated = restaurantService.updateRestaurant(id, resource);
        System.out.println("create restaurant : " + updated.getId());
        URI location_uri = new URI("/user/restaurant/" + updated.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("restaurantId", updated.getId());
        return ResponseEntity.created(location_uri).body(map);
    }

    @DeleteMapping("/user/restaurant/{restaurantId}")
    public ResponseEntity<?> update(
            Authentication authentication,
            @PathVariable("restaurantId") Long id
    ) throws RestaurantNotFoundException, URISyntaxException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        restaurantService.deleteRestaurant(userDetails.getUsername(), id);
        URI location_uri = new URI("/user/restaurant/" + id);
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("restaurantId", id);
        return ResponseEntity.created(location_uri).body(map);
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
