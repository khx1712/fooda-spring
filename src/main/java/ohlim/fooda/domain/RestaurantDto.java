package ohlim.fooda.domain;

import lombok.*;

import javax.persistence.Column;

public class RestaurantDto {

    @Getter
    public  static class RegistRestaurantInfo{
        private Long folderId;
        private String name;
        private String phoneNumber;
        private Double lat;
        private Double lon;
        private String location;
        private Character category;
        private String businessHour;
    }

    @Getter
    public static class changeRestaurantInfo{
        private String name;
        private String phoneNumber;
        private Double lat;
        private Double lon;
        private String location;
        private Character category;
        private String businessHour;
    }

    @Getter
    public  static class ChangeFolderInfo{
        private Long restaurantId;
        private Long beforeFolderId;
        private Long afterFolderId;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MapRestaurantsMeta{
        private Integer page;
        private Boolean is_end;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResRestaurantVO<M, D> {
        private M meta;
        private D documents;
    }

}
