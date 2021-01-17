package ohlim.fooda.service;

import javassist.NotFoundException;
import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Folder;
import ohlim.fooda.domain.RestImage;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.restaurant.RestaurantDetailDto;
import ohlim.fooda.dto.restaurant.RestaurantImageDto;
import ohlim.fooda.dto.restaurant.RestaurantDto;
import ohlim.fooda.dto.restaurant.RestaurantThumbnailDto;
import ohlim.fooda.error.exception.AccountNotFoundException;
import ohlim.fooda.error.exception.InvalidParameterException;
import ohlim.fooda.error.exception.RestaurantNotFoundException;
import ohlim.fooda.repository.AccountRepository;
import ohlim.fooda.repository.FolderRepository;
import ohlim.fooda.repository.RestImageRepository;
import ohlim.fooda.repository.RestaurantRepository;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository, AccountRepository accountRepository,
                             RestImageRepository restImageRepository, FolderRepository folderRepository) {
        this.restaurantRepository = restaurantRepository;
        this.accountRepository = accountRepository;
        this.restImageRepository = restImageRepository;
        this.folderRepository = folderRepository;
    }

    /**
     * 식당 id에 해당하는 식당의 상세정보.
     * @param id 식당 id
     * @return 성공시 응답
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
     * 식당의 이름에 해당하는 식당들의 상세정보를 제공.
     * @param username 사용자 id
     * @param restaurantName 식당 이름
     * @return 성공시 응답
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
     * 식당의 정보와 사진들을 저장한다.
     * @param userName 사용자의 아이디
     * @param restaurantDto 식당 상세정보
     * @param multipartFiles 식당의 이미지 리스트
     * @return 성공시 응답
     */
    public Long addRestaurant(String userName, RestaurantDto restaurantDto, List<MultipartFile> multipartFiles)
            throws ParseException, NotFoundException{
        Account account = accountRepository.findByUserName(userName).orElseThrow(()-> new AccountNotFoundException());
        Folder folder = folderRepository.getFolder(restaurantDto.getFolderId());
        Restaurant restaurant = Restaurant.createRestaurant(restaurantDto, account, folder);
        restaurantRepository.save(restaurant);
        System.out.println(restaurant.getId());
        for(MultipartFile multipartFile: multipartFiles){
            RestImage restImage = RestImage.createRestImage(multipartFile, restaurant);
            restImageRepository.save(restImage);
        }
        return restaurant.getId();
    }

    /**
     * 수정할 식당의 정보를 입력받아 수정한다.
     * @param restaurantId 식당 id
     * @param restaurantDto 수정할 정보
     * @return 성공시 응답
     */
    public Restaurant updateRestaurant(Long restaurantId, RestaurantDto restaurantDto) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantRepository.getRestaurant(restaurantId);
        // TODO: ModelMapper 처리하기
        restaurant.setCategory(restaurantDto.getCategory());
        restaurant.setBusinessHour(restaurantDto.getBusinessHour());
        restaurant.setLocation(restaurantDto.getLocation());
        restaurant.setLatitude(restaurantDto.getLatitude());
        restaurant.setLongitude(restaurant.getLongitude());
        restaurant.setPhoneNumber(restaurantDto.getPhoneNumber());
        restaurant.setName(restaurantDto.getName());
        return restaurant;
    }

    /**
     * 식당 id에 해당하는 식당 삭제.
     * @param username 사용자 id
     * @param id 식당 id
     */
    public void deleteRestaurant(String username, Long id) throws RestaurantNotFoundException {
        Restaurant restaurant = restaurantRepository.findByUserNameAndId(username, id);
        restaurantRepository.delete(restaurant);
    }

    /**
     * Folder id에 해당하는 식당 목록 제공.
     * @param id Folder id
     * @return 성공시 응답
     */
//    public List<Restaurant> getRestaurantByFolderId(Long id) {
//        return restaurantRepository.findAllByFolderId(id);
//    }

    /**
     * 입력받은 lat, lng 에서 가까운 순으로 Size 만큼의 식당의 목록을 제공한다.
     * @param username 사용자 id
     * @param lat 경도
     * @param lng 위도
     * @param page 몇번째 page 인지
     * @param size 하나의 page 크기
     * @return 성공시 응답
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
