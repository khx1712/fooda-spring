package ohlim.fooda.dto;

import lombok.*;
import ohlim.fooda.domain.Restaurant;

import java.util.List;

public class FolderDto {

    @Getter
    public  static class FolderInfo{
        private String name;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public  static class FolderIncludeResInfo{
        private Long id;
        private String name;
        private Integer RestaurantCnt;
        private List<Restaurant> restaurants;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResFolderDto<T, D> extends ResponseDto<T>{
        private T meta;
        private D documents;
    }
}
