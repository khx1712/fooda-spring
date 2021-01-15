package ohlim.fooda.dto.restImage;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RestImageUrlDto {
    private Long restImageId;
    private String fileUrl;
}
