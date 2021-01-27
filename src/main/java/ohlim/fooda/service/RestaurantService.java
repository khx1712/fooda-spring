package ohlim.fooda.service;

import javassist.NotFoundException;
import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Folder;
import ohlim.fooda.domain.RestImage;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.restImage.RestImageDto;
import ohlim.fooda.dto.restaurant.*;
import ohlim.fooda.error.exception.AccountNotFoundException;
import ohlim.fooda.error.exception.InvalidParameterException;
import ohlim.fooda.error.exception.RestaurantNotFoundException;
import ohlim.fooda.repository.AccountRepository;
import ohlim.fooda.repository.FolderRepository;
import ohlim.fooda.repository.RestImageRepository;
import ohlim.fooda.repository.RestaurantRepository;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Transactional
public class RestaurantService {

    private RestaurantRepository restaurantRepository;
    private AccountRepository accountRepository;
    private RestImageRepository restImageRepository;
    private FolderRepository folderRepository;
    private FileUtil fileUtil;
    private LocationUtil locationUtil;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository, AccountRepository accountRepository, FileUtil fileUtil,
                             RestImageRepository restImageRepository, FolderRepository folderRepository, LocationUtil locationUtil) {
        this.restaurantRepository = restaurantRepository;
        this.accountRepository = accountRepository;
        this.restImageRepository = restImageRepository;
        this.folderRepository = folderRepository;
        this.fileUtil = fileUtil;
        this.locationUtil = locationUtil;
    }

    /**
     * 식당 id에 해당하는 식당의 상세정보를 반환합니다.
     * @param id 식당 id
     * @return 식당 상세 정보
     */
    public RestaurantDetailDto getRestaurant(Long id){
        Restaurant restaurant = restaurantRepository.getRestaurant(id);
        return RestaurantDetailDto.createRestaurantDetailDto(restaurant);
    }

    public RestaurantImageDto getRestaurantIncludeImage(Long id) {
        Restaurant restaurant = restaurantRepository.getRestaurantImage(id);
        return RestaurantImageDto.createRestaurantImageDto(restaurant);
    }

    /**
     * 식당의 이름에 해당하는 식당들의 상세정보를 반환합니다.
     * @param username 사용자 id
     * @param restaurantName 식당 이름
     * @return 썸네일을 포함한 식당정보 목록
     */
    public List<RestaurantThumbnailDto> getRestaurantByName(String username, String restaurantName){
        List<Restaurant> restaurants= restaurantRepository.findByUserNameAndRestName(username, restaurantName);
        if(restaurants.isEmpty()){
            throw new RestaurantNotFoundException();
        }
        List<RestaurantThumbnailDto> restaurantThumbnailDtos= new ArrayList<>();
        for(Restaurant restaurant : restaurants){
            restaurantThumbnailDtos.add(RestaurantThumbnailDto.createRestaurantThumbnailDto(restaurant));
        }
        System.out.println(restaurantThumbnailDtos);
        return restaurantThumbnailDtos;
    }

    /**
     * 식당의 정보와 사진들을 저장합니다.
     * @param userName 사용자의 아이디
     * @param restaurantDto 식당 상세정보
     * @param multipartFiles 식당의 이미지 리스트
     * @return 저장된 식당 아이디
     */
    public Long addRestaurant(String userName, RestaurantDto restaurantDto, List<MultipartFile> multipartFiles) throws NotFoundException, ParseException {
        Account account = accountRepository.findByUserName(userName).orElseThrow(()-> new AccountNotFoundException());
        Folder folder = folderRepository.getFolder(restaurantDto.getFolderId());
        Restaurant restaurant = Restaurant.createRestaurant(restaurantDto, account, folder);
        if(restaurant.getLatitude() == null && restaurant.getLongitude() == null){
            LatLngDto latLngDto = locationUtil.locationToGps(restaurant.getLocation());
            restaurant.setLatitude(latLngDto.getLatitude());
            restaurant.setLongitude(latLngDto.getLongitude());
        }
        restaurantRepository.save(restaurant);

        System.out.println(restaurant.getId());
        for(MultipartFile multipartFile: multipartFiles){
            RestImageDto restImageDto = fileUtil.uploadFile(multipartFile, restaurant.getId());
            RestImage restImage = RestImage.createRestImage(restImageDto, restaurant);
            restImageRepository.save(restImage);
            if(restaurant.getThumbnailUrl() == null){
                restaurant.setThumbnailUrl(restImage.getFileThumbnailUrl());
            }
        }
        return restaurant.getId();
    }

    /**
     * 수정할 식당의 정보를 입력받아 수정합니다.
     * @param restaurantId 식당 id
     * @param restaurantDto 수정할 정보
     * @return 수정된 식당 아이디
     */
    public Long updateRestaurant(Long restaurantId, RestaurantDto restaurantDto) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantRepository.getRestaurant(restaurantId);
        // TODO: ModelMapper 처리하기
        restaurant.setCategory(restaurantDto.getCategory());
        restaurant.setBusinessHour(restaurantDto.getBusinessHour());
        restaurant.setLocation(restaurantDto.getLocation());
        restaurant.setLatitude(restaurantDto.getLatitude());
        restaurant.setLongitude(restaurant.getLongitude());
        restaurant.setPhoneNumber(restaurantDto.getPhoneNumber());
        restaurant.setName(restaurantDto.getName());
        return restaurant.getId();
    }

    /**
     * 식당 id에 해당하는 식당 삭제합니다.
     * @param username 사용자 id
     * @param id 식당 id
     */
    public void deleteRestaurant(String username, Long id) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantRepository.findByUserNameAndId(username, id);
        Set<RestImage> restImages = restaurant.getRestImages();
        for(RestImage restImage :restImages){
            fileUtil.deleteFile(restImage.getFilePath());
            //fileUtil.deleteFile(restImage.getFileMiddlePath());
            fileUtil.deleteFile(restImage.getFileThumbnailPath());
        }
        restaurantRepository.delete(restaurant);
    }

    /**
     * 입력받은 lat, lng 에서 가까운 순으로 Size 만큼의 식당의 목록을 반환합니다.
     * @param username 사용자 id
     * @param lat 경도
     * @param lng 위도
     * @param page 몇번째 page 인지
     * @param size 하나의 page 크기
     * @return meta정보, 썸네일을 포함한 식당정보 목록
     */
    public Map<String, Object> getMapRestaurants(String username, Double lat, Double lng, Integer page, Integer size) throws RestaurantNotFoundException, InvalidParameterException {
        Account account = accountRepository.findByUserName(username).orElseThrow(()->new AccountNotFoundException());
        List<Restaurant> restaurants = restaurantRepository.getRestaurantOrderByDist(lat, lng, account.getId());
        int totalCount = restaurants.size();
        int startIdx = (page-1)*size;
        boolean isEnd = false;
        int endIdx = 0;
        int pageCount = (int) Math.ceil((float)totalCount / size);

        if(pageCount < page){
            //throw new InvalidParameterException();
        }else if(pageCount == page){
            isEnd = true;
            endIdx = totalCount;
        }else{
            endIdx = startIdx + size;
        }
        restaurants = restaurants.subList(startIdx, endIdx);

        List<RestaurantThumbnailDto> restaurantThumbnailDtos = new ArrayList<>();
        for(Restaurant restaurant : restaurants){
            restaurantThumbnailDtos.add(RestaurantThumbnailDto.createRestaurantThumbnailDto(restaurant));
        }

        Map<String, Object> meta = new HashMap<>();
        meta.put("total_count",totalCount);
        meta.put("is_end", isEnd);
        meta.put("page",page);

        return  new HashMap<String, Object>(){{
            put("meta", meta);
            put("documents", restaurantThumbnailDtos);
        }} ;
    }

}
