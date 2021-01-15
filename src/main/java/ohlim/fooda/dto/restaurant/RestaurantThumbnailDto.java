package ohlim.fooda.dto.restaurant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ohlim.fooda.domain.Restaurant;

import java.util.List;

@Getter
@Setter
@Builder
public class RestaurantThumbnailDto {
    private Long id;
    private String name;
    private String location;
    private Character category;
    private String businessHour;
    private String thumbnailUrl;

    public static RestaurantThumbnailDto createRestaurantThumbnailDto(Restaurant restaurant){
        // TODO: ModelMapper 처리하기
        return RestaurantThumbnailDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .businessHour(restaurant.getBusinessHour())
                .category(restaurant.getCategory())
                .location(restaurant.getLocation())
                .thumbnailUrl(restaurant.getThumbnailUrl())
                .build();
    }
}
