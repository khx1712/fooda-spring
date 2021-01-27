package ohlim.fooda.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;
import ohlim.fooda.dto.SuccessResponse;
import ohlim.fooda.dto.restaurant.RestaurantDto;
import ohlim.fooda.error.exception.InvalidParameterException;
import ohlim.fooda.service.RestImageService;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ohlim.fooda.error.exception.RestaurantNotFoundException;
import ohlim.fooda.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.util.annotation.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = {"Restaurant API"})
@RestController
public class RestaurantController {

    private Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);
    @Autowired
    RestaurantService restaurantService;
    @Autowired
    RestImageService restImageService;

    @ApiOperation(value = "식당 등록", notes = "식당 정보와 사진으로 새로운 식당을 등록합니다.")
    @PostMapping(value = "/user/restaurant", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart @Validated RestaurantDto resource,
            BindingResult bindingResult,
            @RequestPart List<MultipartFile> files
            ) throws ParseException, NotFoundException, IOException{
        if(bindingResult.hasErrors()) {
            throw new InvalidParameterException(bindingResult);
        }
        Long restaurantId = restaurantService.addRestaurant(userDetails.getUsername(), resource, files);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                .message("'"+restaurantId.toString()+"' 식당을 추가하였습니다.")
                        // TODO: Dto 처리해줄지 고민해보기
                .meta( new HashMap<String, Long>(){{
                    put("restaurantId", restaurantId);
                }}).build()
                , HttpStatus.CREATED);
    }

    @ApiOperation(value = "식당 수정", notes = "식당 정보를 통해 식당을 수정합니다.")
    @PatchMapping("/user/restaurant/{restaurantId}")
    public ResponseEntity<?> update(
            @PathVariable("restaurantId") Long id,
            @RequestBody @Validated RestaurantDto resource,
            BindingResult bindingResult
    ) throws RestaurantNotFoundException {
        if(bindingResult.hasErrors()) {
            throw new InvalidParameterException(bindingResult);
        }
        Long restaurantId = restaurantService.updateRestaurant(id, resource);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                .message("'"+id.toString()+"' 식당을 수정하였습니다.")
                .meta(new HashMap<String, Long>(){{
                    put("restaurantId", restaurantId);
                }}).build()
                ,HttpStatus.OK);
    }

    @ApiOperation(value = "식당 삭제", notes = "해당 id의 식당을 삭제합니다.")
    @DeleteMapping("/user/restaurant/{restaurantId}")
    public ResponseEntity<?> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("restaurantId") Long id
    ) throws RestaurantNotFoundException{
        restaurantService.deleteRestaurant(userDetails.getUsername(), id);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                .message("'"+id.toString()+"' 식당을 삭제하였습니다.").build()
                , HttpStatus.OK);
    }

    @ApiOperation(value = "식당 상세", notes = "해당 id의 식당 상세정보를 제공합니다.")
    @GetMapping("/user/restaurant/{restaurantId}")
    public ResponseEntity<?> detail(
            @PathVariable("restaurantId") Long id
    ) throws RestaurantNotFoundException{
        return new ResponseEntity<>(
                SuccessResponse.builder()
                .message("'"+id.toString()+"' 식당의 상세입니다.")
                .documents(restaurantService.getRestaurant(id)).build()
                , HttpStatus.OK);
    }

    @ApiOperation(value = "식당 상세(이미지 포함)", notes = "해당 id의 식당 상세정보를 제공합니다(이미지 URL 포함).")
    @GetMapping("/user/restaurant/{restaurantId}/restImages")
    public ResponseEntity<?> detailImage(
            @PathVariable("restaurantId") Long id
    ) throws RestaurantNotFoundException{
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("이미지를 포함한 '"+id.toString()+"' 식당의 상세입니다.")
                        .documents(restaurantService.getRestaurantIncludeImage(id)).build()
                , HttpStatus.OK);
    }

    // TODO: 이름과 유사한 식당의 리스트 목록 줄수 있도록 수정 (distance vector 알고리즘 적용)
    @ApiOperation(value = "식당 목록(식당이름)", notes = "해당 식당이름과 일치하거나 유사한 식당목록을 제공합니다(Thumbnail 포함).")
    @GetMapping("/user/restaurants")
    public ResponseEntity<?> listByName(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("name") String name
    ) throws RestaurantNotFoundException{
        return new ResponseEntity<>(
                SuccessResponse.builder()
                .message("'"+name+"' 유저의 식당 목록입니다.")
                .documents(restaurantService.getRestaurantByName(userDetails.getUsername(), name)).build(),
                HttpStatus.OK);
    }

    @ApiOperation(value = "식당 목록(위치)", notes = "해당 좌표와 가까운 식당들의 목록를 제공합니다(Thumbnail 포함).")
    @GetMapping("/user/map/restaurants")
    public ResponseEntity<?> list(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("lat") Double lat,
            @RequestParam("lon") Double lon,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ) throws RestaurantNotFoundException, InvalidParameterException {
        Map<String, Object> body = restaurantService.getMapRestaurants(userDetails.getUsername(),lat, lon, page, size);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                .message("입력받은 좌표에서 가까운 식당 목록입니다.")
                .meta(body.get("meta"))
                .documents(body.get("documents")).build(),
                HttpStatus.OK);
    }
}
