package ohlim.fooda.controller;

import io.lettuce.core.internal.LettuceSets;
import javassist.NotFoundException;
import ohlim.fooda.domain.RestImage;
import ohlim.fooda.dto.SuccessResponse;
import ohlim.fooda.dto.restaurant.RestaurantDto;
import ohlim.fooda.dto.restaurant.RestaurantThumbnailDto;
import ohlim.fooda.error.exception.InvalidParameterException;
import ohlim.fooda.service.RestImageService;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.restaurant.RestaurantDto.*;
import ohlim.fooda.error.exception.RestaurantNotFoundException;
import ohlim.fooda.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
public class RestaurantController {

    private Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);
    @Autowired
    RestaurantService restaurantService;
    @Autowired
    RestImageService restImageService;

    @PostMapping(value = "/user/restaurant", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> create(
            Authentication authentication,
            @RequestPart @Validated RestaurantDto resource,
            BindingResult bindingResult,
            @RequestPart List<MultipartFile> files
            ) throws ParseException, NotFoundException, IOException{

        // Restaurant 조건에 맞지 않는 parameter 입력시 error
        if(bindingResult.hasErrors()) {
            throw new InvalidParameterException(bindingResult);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long restaurantId = restaurantService.addRestaurant(userDetails.getUsername(), resource, files);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                .message("식당을 추가하였습니다.")
                .meta( new HashMap<String, Long>(){{
                    put("restaurantId", restaurantId);
                }}).build()
                , HttpStatus.CREATED);
    }

    @PatchMapping("/user/restaurant/{restaurantId}")
    public ResponseEntity<?> update(
            @PathVariable("restaurantId") Long id,
            @RequestBody @Validated RestaurantDto resource,
            BindingResult bindingResult
    ) throws RestaurantNotFoundException {
        if(bindingResult.hasErrors()) {
            throw new InvalidParameterException(bindingResult);
        }
        return new ResponseEntity<>(
                SuccessResponse.builder()
                .message("식당을 수정하였습니다.")
                .documents(restaurantService.updateRestaurant(id, resource)).build()
                ,HttpStatus.OK);
    }

    @DeleteMapping("/user/restaurant/{restaurantId}")
    public ResponseEntity<?> delete(
            Authentication authentication,
            @PathVariable("restaurantId") Long id
    ) throws RestaurantNotFoundException{
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        restaurantService.deleteRestaurant(userDetails.getUsername(), id);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                .message("식당을 삭제하였습니다.").build()
                , HttpStatus.OK);
    }

    @GetMapping("/user/restaurant/{restaurantId}")
    public ResponseEntity<?> detail(
            @PathVariable("restaurantId") Long id
    ) throws RestaurantNotFoundException{
        return new ResponseEntity<>(
                SuccessResponse.builder()
                .message("식당의 상세입니다.")
                .documents(restaurantService.getRestaurant(id)).build()
                , HttpStatus.OK);
    }

    @GetMapping("/user/restaurants")
    public ResponseEntity<?> detailByName(
            Authentication authentication,
            @RequestParam("name") String name
    ) throws RestaurantNotFoundException{
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return new ResponseEntity<>(
                SuccessResponse.builder()
                .message("'"+name+"' "+"식당 리스트 입니다.")
                .documents(restaurantService.getRestaurantByName(userDetails.getUsername(), name)).build(),
                HttpStatus.OK);
    }

    @GetMapping("/user/map/restaurants")
    public ResponseEntity<?> list(
            Authentication authentication,
            @RequestParam("lat") Double lat,
            @RequestParam("lon") Double lon,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ) throws RestaurantNotFoundException, InvalidParameterException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Map<String, Object> body = restaurantService.getMapRestaurants(userDetails.getUsername(),lat, lon, page, size);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                .message("식당 리스트 입니다.")
                .meta(body.get("meta"))
                .documents(body.get("documents")).build(),
                HttpStatus.OK);
    }
}
