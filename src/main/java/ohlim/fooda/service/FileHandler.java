package ohlim.fooda.service;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
