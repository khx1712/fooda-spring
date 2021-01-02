package ohlim.fooda.controller;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.RestaurantDto;
import ohlim.fooda.service.OcrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OcrController {
    private Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);
    @Autowired
    OcrService ocrService;

    @PostMapping("/user/ocr")
    ResponseEntity<?> captureOcr(
            Authentication authentication,
            @RequestPart MultipartFile capture
    ) throws IOException, TesseractException, URISyntaxException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Restaurant restaurant = ocrService.extractCapture(capture, userDetails.getUsername());
        URI location_uri = new URI("/user/restaurant/" + restaurant.getId());
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "레스토랑을 생성하였습니다.");
        meta.put("restaurantId", restaurant.getId());
        RestaurantDto.ResRestaurantDto<?,?> resRestaurantDto = RestaurantDto.ResRestaurantDto.builder()
                .meta(meta)
                .build();
        return ResponseEntity.created(location_uri).body(resRestaurantDto);
    }
}
