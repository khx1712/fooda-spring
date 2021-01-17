package ohlim.fooda.domain;

import lombok.*;
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

    public static RestImage createRestImage(MultipartFile multipartFile, Restaurant restaurant) {
        String fileOriginName = multipartFile.getOriginalFilename();
        String contentType = multipartFile.getContentType();
        String[] nameArray = fileOriginName.split("\\.");
        String ext = nameArray[nameArray.length - 1];
        String fileSaveName = getFileSaveName(ext, restaurant.getId().toString() + "_" + restaurant.getName());
        String urlPath = getFileUploadPath() + "/" + fileSaveName;
        File dest = new File(urlPath);

        try {
            System.out.println(multipartFile.getInputStream().getClass());
            InputStream fileStream = multipartFile.getInputStream();
            // TODO: 이미지를 copy 한뒤에 오류로 종료될 때 이미지를 삭제 처리해주기 (DB에는 안들어감)
            FileUtils.copyInputStreamToFile(fileStream, dest);
        } catch (IOException e) {
            FileUtils.deleteQuietly(dest);
            e.printStackTrace();
        }

        RestImage restImage = RestImage.builder()
                .fileOriginName(fileOriginName)
                .filePath(dest.getPath())
                .fileUrl("/" + urlPath)
                .fileSaveName(fileSaveName)
                .fileExt(ext)
                .contentType(contentType)
                .build();
        restImage.setRestaurant(restaurant);

        return restImage;
    }

    public static String makeDirectory(LocalDateTime localDate){
        final String SAVE_WINDOW_PATH = "C:/restImages";
        final String SAVE_LINUX_PATH = "/restImages";
        final String PREFIX_URL = "restImages/";

        File dir;
        String yearPath = PREFIX_URL + Integer.toString(localDate.getYear());
        dir = new File(yearPath);
        if(!dir.exists()){
            dir.mkdirs();
            System.out.println("created yearDirectory!");
        }
        String monthPath = yearPath + "/" + Integer.toString(localDate.getMonthValue());
        dir = new File(monthPath);
        if(!dir.exists()){
            dir.mkdirs();
            System.out.println("created monthDirectory!");
        }
        String dayPath = monthPath + "/" + Integer.toString(localDate.getDayOfMonth());
        dir = new File(dayPath);
        if(!dir.exists()){
            dir.mkdirs();
            System.out.println("created dayDirectory!");
        }
        return  dayPath;
    }

    public static String getFileUploadPath(){
        LocalDateTime dateTime = LocalDateTime.now();
        return makeDirectory(dateTime);
    }

    public static String getFileSaveName(String ext, String fileName){
        LocalDateTime dateTime = LocalDateTime.now();
        fileName += "_"
                + Integer.toString(dateTime.getHour())
                + Integer.toString(dateTime.getMinute())
                + Integer.toString(dateTime.getSecond())
                + Integer.toString(dateTime.getNano())
                + "." + ext;
        return fileName;
    }
}
