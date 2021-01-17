package ohlim.fooda.service;

import ohlim.fooda.domain.RestImage;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.restImage.RestImageDto;
import ohlim.fooda.dto.restImage.RestImageUrlDto;
import ohlim.fooda.error.exception.RestImageNotFoundException;
import ohlim.fooda.repository.RestImageRepository;
import ohlim.fooda.repository.RestaurantRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RestImageService {

    private RestImageRepository restImageRepository;
    private RestaurantRepository restaurantRepository;

    @Autowired
    public RestImageService(RestImageRepository restImageRepository, RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
        this.restImageRepository = restImageRepository;
    }

    public void deleteRestImage(Long id) throws RestImageNotFoundException {
        RestImage restImage = restImageRepository.getRestImage(id);
        String filePath = restImage.getFilePath();
        File deleteFile = new File(filePath);
        if( deleteFile.exists() ){
            if(deleteFile.delete()){
                System.out.println("파일삭제 성공");
            }else{
                System.out.println("파일삭제 실패");
            }
        }else{
            System.out.println("파일이 존재하지 않습니다.");
        }
        restImageRepository.delete(restImage);
    }

    public List<RestImageUrlDto> fileUpload(List<MultipartFile> multipartFiles,String userName,Long restaurantId){
        Restaurant restaurant = restaurantRepository.findByUserNameAndId(userName, restaurantId);
        List<RestImageUrlDto> restImageUrlDtos = new ArrayList<>();
        for(MultipartFile multipartFile: multipartFiles){
            RestImage restImage = RestImage.createRestImage(multipartFile, restaurant);
            restImageRepository.save(restImage);
            restImageUrlDtos.add(RestImageUrlDto.createRestImageUrlDto(restImage));
        }
        return restImageUrlDtos;
    }

    public List<RestImageUrlDto> getRestImagesByRestId(Long id) {
        List<RestImage> restImages = restImageRepository.findAllByRestaurantId(id);
        List<RestImageUrlDto> restImageUrlDtos = new ArrayList<>();
        for(RestImage restImage: restImages){
            restImageUrlDtos.add(RestImageUrlDto.createRestImageUrlDto(restImage));
        }
        return restImageUrlDtos;
    }

    public RestImageDto getRestImage(Long id) {
        RestImage restImage = restImageRepository.getRestImage(id);
        return RestImageDto.createRestImageDto(restImage);
    }
}

