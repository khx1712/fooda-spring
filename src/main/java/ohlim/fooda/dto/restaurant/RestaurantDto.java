package ohlim.fooda.dto.restaurant;

import lombok.*;
import ohlim.fooda.domain.RestImage;
import ohlim.fooda.domain.Restaurant;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {
    private Long folderId;
    private String name;
    private String phoneNumber;
    private Double latitude;
    private Double longitude;
    private String location;
    private Character category;
    private String businessHour;

    public static RestaurantDto create(Restaurant restaurant){
        // TODO: ModelMapper 처리하기
        return RestaurantDto.builder()
                .name(restaurant.getName())
                .category(restaurant.getCategory())
                .businessHour(restaurant.getBusinessHour())
                .location(restaurant.getLocation())
                .phoneNumber(restaurant.getPhoneNumber())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .build();
    }
}
