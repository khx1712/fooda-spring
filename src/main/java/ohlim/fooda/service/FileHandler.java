package ohlim.fooda.service;

import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

// TODO: component 로 제공했을 때  이점을 생각해보기
//  component 는 entity 말고 service 에서 사용되는게 좋으므로 그 방식으로 코드 수정 요망
@Component
public class FileHandler {
    public static final String SAVE_WINDOW_PATH = "C:/restImages";
    public static final String SAVE_LINUX_PATH = "/restImages";
    public static final String PREFIX_URL = "restImages/";

    public static String makeDirectory(LocalDateTime localDate){
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
