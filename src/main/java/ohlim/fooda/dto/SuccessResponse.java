package ohlim.fooda.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse<T,D>{
    final private Boolean success = true;
    private String message;
    private T meta;
    private D documents;
}
