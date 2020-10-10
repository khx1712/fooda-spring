package ohlim.fooda.service;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FileHandler {
    public static final String SAVE_WINDOW_PATH = "C:/upload";
    public static final String SAVE_LINUX_PATH = "/upload";
    public static final String PREFIX_URL = "/upload/";

    public static void makeDirectory(LocalDateTime localDate){
        File dir;
        String yearPath = SAVE_WINDOW_PATH + "/" + Integer.toString(localDate.getYear());
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
    }

    public static String getFileUploadPath(){
        LocalDateTime dateTime = LocalDateTime.now();
        makeDirectory(dateTime);
        String filePath = SAVE_WINDOW_PATH
                + "/" + Integer.toString(dateTime.getYear())
                + "/" + Integer.toString(dateTime.getMonthValue())
                + "/" + Integer.toString(dateTime.getDayOfMonth());
        return filePath;
    }

    public static String getFileSaveName(String ext, String userName){
        LocalDateTime dateTime = LocalDateTime.now();
        String fileName = userName + "_"
                + Integer.toString(dateTime.getHour())
                + Integer.toString(dateTime.getMinute())
                + Integer.toString(dateTime.getSecond())
                + Integer.toString(dateTime.getNano())
                + "." + ext;
        return fileName;
    }
}
