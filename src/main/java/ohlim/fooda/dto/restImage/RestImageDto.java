package ohlim.fooda.dto.restImage;

import lombok.*;
import ohlim.fooda.domain.RestImage;

import javax.persistence.Column;

@Getter
@Setter
@Builder
public class RestImageDto {
    private Long id;
    private String fileSaveName;
    private String fileOriginName;
    private String filePath;
    private String fileUrl;
    private String fileExt;
    private  String contentType;

    public static RestImageDto createRestImageDto(RestImage restImage){
        // TODO: ModelMapper 처리하기
        return RestImageDto.builder()
                .id(restImage.getId())
                .fileSaveName(restImage.getFileSaveName())
                .fileOriginName(restImage.getFileOriginName())
                .filePath(restImage.getFilePath())
                .contentType(restImage.getContentType())
                .fileUrl(restImage.getFileUrl())
                .fileExt(restImage.getFileExt())
                .build();
    }

}
