package ohlim.fooda.dto.folder;

import lombok.*;
import ohlim.fooda.domain.Folder;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.restaurant.RestaurantThumbnailDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderRestaurantDto {

    private Long id;
    private String name;
    private Integer RestaurantCnt;
    private List<RestaurantThumbnailDto> restaurants;

    public static FolderRestaurantDto create(Folder folder){
        // TODO: ModelMapper 처리하기
        List<RestaurantThumbnailDto> restaurantThumbnailDtos = new ArrayList<>();
        for(Restaurant restaurant : folder.getRestaurants()){
            restaurantThumbnailDtos.add(RestaurantThumbnailDto.create(restaurant));
        }
        return FolderRestaurantDto.builder()
                .id(folder.getId())
                .name(folder.getName())
                .RestaurantCnt(folder.getRestaurants().size())
                .restaurants(restaurantThumbnailDtos)
                .build();
    }
}
