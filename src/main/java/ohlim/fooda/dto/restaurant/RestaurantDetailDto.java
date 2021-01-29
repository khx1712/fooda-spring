package ohlim.fooda.dto.restaurant;

import lombok.*;
import ohlim.fooda.domain.RestImage;
import ohlim.fooda.domain.Restaurant;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDetailDto {
    private Long restaurantId;
    private Long folderId;
    private String name;
    private String phoneNumber;
    private Double latitude;
    private Double longitude;
    private String location;
    private Character category;
    private String businessHour;
    private String thumbnailUrl;

    public static RestaurantDetailDto create(Restaurant restaurant){
        // TODO: ModelMapper 처리하기
        return RestaurantDetailDto.builder()
                .restaurantId(restaurant.getId())
                .name(restaurant.getName())
                .category(restaurant.getCategory())
                .businessHour(restaurant.getBusinessHour())
                .location(restaurant.getLocation())
                .phoneNumber(restaurant.getPhoneNumber())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .thumbnailUrl(restaurant.getThumbnailUrl())
                .build();
    }
}