package ohlim.fooda.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ohlim.fooda.dto.SuccessResponse;
import ohlim.fooda.error.exception.RestImageNotFoundException;
import ohlim.fooda.service.RestImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = {"RestImage API"})
@RestController
public class RestImageController {

    private Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);

    @Autowired
    RestImageService restImageService;

    @ApiOperation(value = "식당사진 등록", notes = "해당 id의 식당에 사진들을 등록합니다.")
    @PostMapping("/user/images/{restaurantId}")
    public ResponseEntity<?> upload(
            Authentication authentication,
            @PathVariable("restaurantId") Long id,
            @RequestPart List<MultipartFile> files
            ){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("Id "+ id.toString() + " 식당에 다음 이미지 목록을 저장하였습니다.")
                        .documents(restImageService.fileUpload(files, userDetails.getUsername(), id)).build()
                , HttpStatus.OK);
    }

    @ApiOperation(value = "식당사진 목록", notes = "해당 id의 식당 해당하는 사진목록을 제공합니다.")
    @GetMapping("/user/images/{restaurantId}")
    public ResponseEntity<?> listByRestId(
            Authentication authentication,
            @PathVariable("restaurantId") Long id
    ){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("Id "+ id.toString() + " 식당에 해당하는 이미지 목록입니다.")
                        .documents(restImageService.getRestImagesByRestId(id)).build()
                , HttpStatus.OK);
    }

    @ApiOperation(value = "식당사진 상세", notes = "해당 id의 사진 상세정보를 제공합니다.")
    @GetMapping("/user/image/{restImageId}")
    public ResponseEntity<?> detail(
            Authentication authentication,
            @PathVariable("restaurantId") Long id
    ){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("Id "+ id.toString() + " 이미지의 상세입니다.")
                        .documents(restImageService.getRestImage(id)).build()
                , HttpStatus.OK);
    }

    @ApiOperation(value = "식당사진 삭제", notes = "해당 id의 사진을 삭제합니다.")
    @DeleteMapping("/user/image/{imageId}")
    public ResponseEntity<?> delete(
            Authentication authentication,
            @PathVariable("imageId") Long id
    ) throws RestImageNotFoundException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        restImageService.deleteRestImage(id);
        return  new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("Id "+ id.toString() + " 이미지를 삭제하였습니다.")
                        .build()
                , HttpStatus.OK);
    }
}
