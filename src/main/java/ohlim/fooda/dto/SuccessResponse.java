package ohlim.fooda.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class SuccessResponse<T,D>{
    final private Boolean success = true;
    private String message;
    private T meta;
    private D documents;
}
