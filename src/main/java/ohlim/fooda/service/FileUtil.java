package ohlim.fooda.service;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ohlim.fooda.domain.RestImage;
import ohlim.fooda.dto.restImage.RestImageDto;
import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

// TODO: component 로 제공했을 때  이점을 생각해보기
//  component 는 entity 말고 service 에서 사용되는게 좋으므로 그 방식으로 코드 수정 요망
@Component
public class FileUtil {
    public static final String SAVE_WINDOW_PATH = "C:/restImages";
    public static final String SAVE_LINUX_PATH = "/restImages";
    public static final String PREFIX_URL = "restImages/";

    public RestImageDto uploadFile(MultipartFile multipartFile, Long restaurantId){
        LocalDateTime dateTime = LocalDateTime.now();
        String fileOriginName = multipartFile.getOriginalFilename();
        String contentType = multipartFile.getContentType();
        String[] nameArray = fileOriginName.split("\\.");
        String fileExt = nameArray[nameArray.length - 1];
        String fileSaveName = getFileSaveName(fileExt, restaurantId.toString(), dateTime);
        String dirPath = makeDirectory(dateTime);
        String fileUrl = dirPath + "/" + fileSaveName;
        File dest = new File(fileUrl);
        FilePathUrl thumbFilePathUrl = null;

        try {
            System.out.println(multipartFile.getInputStream().getClass());
            InputStream fileStream = multipartFile.getInputStream();
            // TODO: 이미지를 copy 한뒤에 오류로 종료될 때 이미지를 삭제 처리해주기 (DB에는 안들어감)
            FileUtils.copyInputStreamToFile(fileStream, dest);
            thumbFilePathUrl = makeThumbnailFile(dirPath, fileSaveName, fileExt);
        } catch (IOException e) {
            FileUtils.deleteQuietly(dest);
            e.printStackTrace();
        }

        return RestImageDto.builder()
                .fileOriginName(fileOriginName)
                .fileSaveName(fileSaveName)
                .fileExt(fileExt)
                .filePath(dest.getAbsolutePath())
                .fileUrl("/" + fileUrl)
                .fileMiddlePath("test")
                .fileMiddleUrl("test")
                .fileThumbnailPath(thumbFilePathUrl.getFilePath())
                .fileThumbnailUrl(thumbFilePathUrl.getFileUrl())
                .contentType(contentType)
                .build();
    }

    public FilePathUrl makeThumbnailFile(String dirPath, String fileSaveName, String fileExt) throws IOException {
        File file = new File(dirPath + "/" + fileSaveName);
        BufferedImage srcImg = ImageIO.read(file);
        
        int dw = 250, dh = 150;
        
        int originWidth = srcImg.getWidth();
        int originHeight = srcImg.getHeight();
        
        int cropWeight = originWidth;
        int cropHeight = (originWidth * dh) / dw;
        if(cropHeight > originHeight) {
            cropWeight = (originHeight * dw) / dh;
            cropHeight = originHeight;
        } 

        BufferedImage cropImg = Scalr.crop(srcImg, (originWidth-cropWeight)/2,
                (originHeight-cropHeight)/2, cropWeight, cropHeight);

        BufferedImage destImg = Scalr.resize(cropImg, dw, dh);

        String thumbUrl = dirPath + "/THUMB_" + fileSaveName;
        File thumbFile = new File(thumbUrl);
        ImageIO.write(destImg, fileExt.toUpperCase(), thumbFile);

        return FilePathUrl.builder()
                .filePath(thumbFile.getAbsolutePath())
                .fileUrl("/"+ thumbUrl)
                .build();
    }

    public void deleteFile(String filePath){
        File file = new File(filePath);
        if( file.exists() ){
            if(file.delete()){
                System.out.println("파일삭제 성공");
            }else{
                System.out.println("파일삭제 실패"); }
        }else{
            System.out.println("파일이 존재하지 않습니다.");
        }
    }

    private String makeDirectory(LocalDateTime localDate){
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

    private String getFileSaveName(String ext, String fileName, LocalDateTime dateTime){
        fileName += "_"
                + Integer.toString(dateTime.getHour())
                + Integer.toString(dateTime.getMinute())
                + Integer.toString(dateTime.getSecond())
                + Integer.toString(dateTime.getNano())
                + "." + ext;
        return fileName;
    }

    @Getter
    @Setter
    @Builder
    private static class FilePathUrl{
        String filePath;
        String fileUrl;
    }
}
