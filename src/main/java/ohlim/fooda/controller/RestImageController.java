package ohlim.fooda.controller;

import ohlim.fooda.service.RestImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.web.bind.annotation.*;

@RestController
public class RestImageController {

    private Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);

    @Autowired
    RestImageService restImageService;

//    @PostMapping("/user/images/{restaurantId}")
//    public ResponseEntity<?> upload(
//            Authentication authentication,
//            @PathVariable("restaurantId") Long id,
//            @RequestPart List<MultipartFile> files
//            ) throws IOException {
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        List<RestImage> restImages = new ArrayList<>();
//        for(MultipartFile multipartFile: files) {
//            RestImage restImage = restImageService.fileUpload(multipartFile, userDetails.getUsername(), id);
//            restImages.add(restImage);
//        }
//        Map<String, Object> meta = new HashMap<>();
//        meta.put("success", true);
//        meta.put("msg", "이미지들이 저장되었습니다.");
//        meta.put("restImageCnt", restImages.size());
//        ResRestImageSuccess<?,?> resRestImageDto = ResRestImageSuccess.builder()
//                .meta(meta)
//                .documents(restImages)
//                .build();
//        return ResponseEntity.ok(resRestImageDto);
//    }
//
//    @DeleteMapping("/user/images/{imageId}")
//    public ResponseEntity<?> delete(
//            Authentication authentication,
//            @PathVariable("imageId") Long id
//    ) throws RestImageNotFoundException {
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        restImageService.deleteRestImage(userDetails.getUsername(), id);
//        Map<String, Object> meta = new HashMap<>();
//        meta.put("success", true);
//        meta.put("msg", "이미지가 삭제되었습니다.");
//        meta.put("restImageId", id);
//        ResRestImageSuccess<?,?> resRestImageDto = ResRestImageSuccess.builder()
//                .meta(meta)
//                .build();
//        return ResponseEntity.ok(resRestImageDto);
//    }
}
