package ohlim.fooda.dto.restaurant;

import lombok.*;
import ohlim.fooda.domain.RestImage;
import ohlim.fooda.domain.Restaurant;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {
    @NotNull(message = "폴더는 필수 값입니다.")
    private Long folderId;
    @NotNull(message = "식당이름은 필수 값입니다.")
    private String name;
    private String phoneNumber;
    private Double latitude;
    private Double longitude;
    @NotNull(message = "주소는 필수 값입니다.")
    private String location;
    @NotNull(message = "카테고리는 필수 값입니다.")
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
