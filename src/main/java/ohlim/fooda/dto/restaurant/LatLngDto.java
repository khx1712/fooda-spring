package ohlim.fooda.dto.restaurant;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LatLngDto {
    private Double latitude;
    private Double longitude;

    public static LatLngDto create(Double latitude, Double longitude){
        return LatLngDto.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
