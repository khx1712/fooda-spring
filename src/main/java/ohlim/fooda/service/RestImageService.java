package ohlim.fooda.service;

import ohlim.fooda.domain.RestImage;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.restImage.RestImageDto;
import ohlim.fooda.dto.restImage.RestImageUrlDto;
import ohlim.fooda.error.exception.RestImageNotFoundException;
import ohlim.fooda.repository.RestImageRepository;
import ohlim.fooda.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    /**
     * 이미지 id에 해당하는 이미지를 삭제합니다.
     * @param id 이미지 id
     */
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

    /**
     * multipartFiles을 받아 저장하고 해당하는 식당에 참조시킵니다.
     * @param multipartFiles 이미지 files
     * @param userName 유저 아이디
     * @param restaurantId 식당 아이디
     * @return 저장된 이미지의 url정보 목록
     */
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

    /**
     * 식당 id에 해당하는 이미지 목록을 반환합니다.
     * @param id 식당 아이디
     * @return 검색된 이미지의 url정보 목록
     */
    public List<RestImageUrlDto> getRestImagesByRestId(Long id) {
        List<RestImage> restImages = restImageRepository.findAllByRestaurantId(id);
        List<RestImageUrlDto> restImageUrlDtos = new ArrayList<>();
        for(RestImage restImage: restImages){
            restImageUrlDtos.add(RestImageUrlDto.createRestImageUrlDto(restImage));
        }
        return restImageUrlDtos;
    }

    /**
     * 식당 id에 해당하는 이미지 정보를 반환합니다.
     * @param id 식당 아이디
     * @return 해당하는 이미지정보
     */
    public RestImageDto getRestImage(Long id) {
        RestImage restImage = restImageRepository.getRestImage(id);
        return RestImageDto.createRestImageDto(restImage);
    }
}

