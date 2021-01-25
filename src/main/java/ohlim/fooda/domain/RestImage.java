package ohlim.fooda.domain;

import lombok.*;
import ohlim.fooda.dto.restImage.RestImageDto;
import org.apache.commons.io.FileUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rest_image_id")
    private Long id;

    @Column(nullable = false, name = "file_save_name") // 실제 디스크에 저장되는 이름
    private String fileSaveName;

    @Column(nullable = false, name = "file_origin_name") // 파일의 원래 이름
    private String fileOriginName;

    @Column(nullable = false, name = "file_path") // 파일의 저장 경로
    private String filePath;

    @Column(nullable = false, name = "file_url") // 파일의 URL
    private String fileUrl;

    @Column(nullable = false, name = "file_middle_path") // 파일의 저장 경로
    private String fileMiddlePath;

    @Column(nullable = false, name = "file_middle_url") // 파일의 URL
    private String fileMiddleUrl;

    @Column(nullable = false, name = "file_thumbnail_path") // 파일의 저장 경로
    private String fileThumbnailPath;

    @Column(nullable = false, name = "file_thumbnail_url") // 파일의 URL
    private String fileThumbnailUrl;

    @Column(nullable = false, name = "file_ext") // 파일 확장자
    private String fileExt;

    @Column(nullable = false, name = "content_type")
    private  String contentType;

    @CreationTimestamp
    @Column(name = "register_date")
    private Date registerDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public void setRestaurant(Restaurant restaurant){
        if(this.restaurant != null) {
            this.restaurant.getRestImages().remove(this);
        }
        this.restaurant = restaurant;
        restaurant.getRestImages().add(this);
    }

    public static RestImage createRestImage(RestImageDto restImageDto, Restaurant restaurant) {
        // TODO: ModelMapper 처리하기
        RestImage restImage = RestImage.builder()
                .fileOriginName(restImageDto.getFileOriginName())
                .filePath(restImageDto.getFilePath())
                .fileUrl(restImageDto.getFileUrl())
                .fileSaveName(restImageDto.getFileSaveName())
                .fileExt(restImageDto.getFileExt())
                .fileMiddlePath(restImageDto.getFileMiddlePath())
                .fileMiddleUrl(restImageDto.getFileMiddleUrl())
                .fileThumbnailPath(restImageDto.getFileThumbnailPath())
                .fileThumbnailUrl(restImageDto.getFileThumbnailUrl())
                .contentType(restImageDto.getContentType())
                .build();
        restImage.setRestaurant(restaurant);
        return restImage;
    }
}
