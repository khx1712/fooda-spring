package ohlim.fooda.dto.restImage;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ohlim.fooda.domain.RestImage;

@Getter
@Setter
@Builder
public class RestImageUrlDto {
    private Long restImageId;
    private String fileUrl;

    public static RestImageUrlDto create(RestImage restImage) {
        // TODO: ModelMapper 처리하기
        return RestImageUrlDto.builder()
                .restImageId(restImage.getId())
                .fileUrl(restImage.getFileUrl())
                .build();
    }
}
