package ohlim.fooda.dto;

import lombok.*;
import ohlim.fooda.domain.RestImage;
import ohlim.fooda.domain.Restaurant;

import javax.persistence.Column;
import java.util.List;

public class RestaurantDto {

    @Getter
    public  static class RestaurantInfo{
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
    @Setter
    @Builder
    public static class RestaurantImageInfo{
        private Restaurant restaurant;
        private List<RestImage> images;
    }

    @Getter
    @Setter
    @Builder
    public static class RestaurantImageInfo2{
        private Long folderId;
        private String name;
        private String phoneNumber;
        private Double lat;
        private Double lon;
        private String location;
        private Character category;
        private String businessHour;
        private List<RestImage> images;
    }

    @Getter
    @Setter
    @Builder
    public static class RestaurantImageUrlInfo{
        private Restaurant restaurant;
        private List<Object> imageUrls;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResRestaurantDto<T, D> extends ResponseDto<T>{
        private T meta;
        private D documents;
    }

}
