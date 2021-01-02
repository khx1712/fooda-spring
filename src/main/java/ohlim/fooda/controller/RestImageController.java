package ohlim.fooda.controller;

import ohlim.fooda.domain.RestImage;
import ohlim.fooda.dto.RestImageDto.*;
import ohlim.fooda.service.RestImageNotFoundException;
import ohlim.fooda.service.RestImageService;
import ohlim.fooda.service.RestaurantService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RestImageController {

    private Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);

    @Autowired
    RestImageService restImageService;

    @PostMapping("/user/images/{restaurantId}")
    public ResponseEntity<?> upload(
            Authentication authentication,
            @PathVariable("restaurantId") Long id,
            @RequestPart List<MultipartFile> files
            ) throws IOException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<RestImage> restImages = new ArrayList<>();
        for(MultipartFile multipartFile: files) {
            RestImage restImage = restImageService.fileUpload(multipartFile, userDetails.getUsername(), id);
            restImages.add(restImage);
        }
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "이미지들이 저장되었습니다.");
        meta.put("restImageCnt", restImages.size());
        ResRestImageDto<?,?> resRestImageDto = ResRestImageDto.builder()
                .meta(meta)
                .documents(restImages)
                .build();
        return ResponseEntity.ok(resRestImageDto);
    }

    @DeleteMapping("/user/images/{imageId}")
    public ResponseEntity<?> delete(
            Authentication authentication,
            @PathVariable("imageId") Long id
    ) throws RestImageNotFoundException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        restImageService.deleteRestImage(userDetails.getUsername(), id);
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "이미지가 삭제되었습니다.");
        meta.put("restImageId", id);
        ResRestImageDto<?,?> resRestImageDto = ResRestImageDto.builder()
                .meta(meta)
                .build();
        return ResponseEntity.ok(resRestImageDto);
    }
}
