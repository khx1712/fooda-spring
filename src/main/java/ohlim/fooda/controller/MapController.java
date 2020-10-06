package ohlim.fooda.controller;

import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.RestaurantDto.*;
import ohlim.fooda.service.RestaurantService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MapController {

    private RestaurantService restaurantService;

    @GetMapping("/user/map/restaurants")
    public ResRestaurantDto<?,?> list(
            Authentication authentication,
            @RequestParam("lat") Double lat,
            @RequestParam("lon") Double lon,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return restaurantService.getMapRestaurants(userDetails.getUsername(),lat, lon, page, size );
    }
}
