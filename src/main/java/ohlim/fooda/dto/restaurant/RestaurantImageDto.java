package ohlim.fooda.dto.restaurant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ohlim.fooda.domain.RestImage;
import ohlim.fooda.domain.Restaurant;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class RestaurantImageDto {
    private String name;
    private String phoneNumber;
    private Double latitude;
    private Double longitude;
    private String location;
    private Character category;
    private String businessHour;
    List<String> imageUrls;

    public static RestaurantImageDto create(Restaurant restaurant){
        // TODO: ModelMapper 처리하기
        List<String> restImageUrls = new ArrayList<>();
        for(RestImage restImage : restaurant.getRestImages()){
            restImageUrls.add(restImage.getFileUrl());
        }
        return RestaurantImageDto.builder()
                .name(restaurant.getName())
                .category(restaurant.getCategory())
                .businessHour(restaurant.getBusinessHour())
                .location(restaurant.getLocation())
                .phoneNumber(restaurant.getPhoneNumber())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .imageUrls(restImageUrls)
                .build();
    }
}
