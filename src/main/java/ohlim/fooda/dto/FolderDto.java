package ohlim.fooda.dto;

import lombok.*;

public class FolderDto {


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
