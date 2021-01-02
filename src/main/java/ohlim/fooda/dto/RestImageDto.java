package ohlim.fooda.dto;

import lombok.*;

public class RestImageDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResRestImageDto<T, D> extends ResponseDto<T>{
        private T meta;
        private D documents;
    }
}
