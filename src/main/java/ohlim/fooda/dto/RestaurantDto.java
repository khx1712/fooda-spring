package ohlim.fooda.dto;

import lombok.*;

import javax.persistence.Column;

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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResRestaurantDto<T, D> extends ResponseDto<T>{
        private T meta;
        private D documents;
    }

}
