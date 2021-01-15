package ohlim.fooda.dto.restaurant;

import lombok.*;
import ohlim.fooda.domain.RestImage;
import ohlim.fooda.domain.Restaurant;

import java.util.List;

@Getter
@Setter
@Builder
public class RestaurantDto {
    private Long folderId;
    private String name;
    private String phoneNumber;
    private Double latitude;
    private Double longitude;
    private String location;
    private Character category;
    private String businessHour;
}
