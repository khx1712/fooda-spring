package ohlim.fooda.dto.folder;

import lombok.*;
import ohlim.fooda.domain.Restaurant;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderIncludeRestDto {

    private Long id;
    private String name;
    private Integer RestaurantCnt;
    private List<Restaurant> restaurants;

}
