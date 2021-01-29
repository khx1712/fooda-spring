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
    private String fileMiddlePath;
    private String fileMiddleUrl;
    private String fileThumbnailPath;
    private String fileThumbnailUrl;
    private String contentType;

    public static RestImageDto create(RestImage restImage){
        // TODO: ModelMapper 처리하기
        return RestImageDto.builder()
                .id(restImage.getId())
                .fileSaveName(restImage.getFileSaveName())
                .fileOriginName(restImage.getFileOriginName())
                .contentType(restImage.getContentType())
                .fileExt(restImage.getFileExt())
                .fileUrl(restImage.getFileUrl())
                .filePath(restImage.getFilePath())
                .fileMiddlePath(restImage.getFileMiddlePath())
                .fileMiddleUrl(restImage.getFileMiddleUrl())
                .fileThumbnailPath(restImage.getFileThumbnailPath())
                .fileThumbnailUrl(restImage.getFileThumbnailUrl())
                .build();
    }

}
