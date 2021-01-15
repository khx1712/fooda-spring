package ohlim.fooda.service;

import ohlim.fooda.domain.RestImage;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.error.exception.RestImageNotFoundException;
import ohlim.fooda.repository.RestImageRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class RestImageService {

    private RestImageRepository restImageRepository;

    @Autowired
    public RestImageService(RestImageRepository restImageRepository) {
        this.restImageRepository = restImageRepository;
    }

//    public void deleteRestImage(String username, Long id) throws RestImageNotFoundException {
//        RestImage restImage = restImageRepository.findAllByIdAndUserName(id, username)
//                .orElseThrow(()-> new RestImageNotFoundException());
//        String filePath = restImage.getFilePath();
//        File deleteFile = new File(filePath);
//        if( deleteFile.exists() ){
//            if(deleteFile.delete()){
//                System.out.println("파일삭제 성공");
//            }else{
//                System.out.println("파일삭제 실패");
//            }
//        }else{
//            System.out.println("파일이 존재하지 않습니다.");
//        }
//        restImageRepository.delete(restImage);
//    }

//    public List<RestImage> fileUpload(List<MultipartFile> multipartFiles, Restaurant restaurant) throws IOException {
//        List<RestImage> restImages = new ArrayList<>();
//        for(MultipartFile multipartFile: multipartFiles) {
//            String fileOriginName = multipartFile.getOriginalFilename();
//            String contestType = multipartFile.getContentType();
//            String[] nameArray = fileOriginName.split("\\.");
//            String ext = nameArray[nameArray.length - 1];
//            String fileSaveName = FileHandler.getFileSaveName(ext, restaurant.getId().toString()+"_"+restaurant.getName());
//            String urlPath = FileHandler.getFileUploadPath() + "/" + fileSaveName;
//            File dest = new File(urlPath);
//
//            try {
//                System.out.println(multipartFile.getInputStream().getClass());
//                InputStream fileStream = multipartFile.getInputStream();
//                FileUtils.copyInputStreamToFile(fileStream, dest);
//            } catch (IOException e) {
//                FileUtils.deleteQuietly(dest);
//                e.printStackTrace();
//            }
//
//            RestImage restImage = RestImage.builder()
//                    .fileOriginName(fileOriginName)
//                    .restaurant(restaurant)
//                    .filePath(dest.getPath())
//                    .fileUrl("/" + urlPath)
//                    .fileSaveName(fileSaveName)
//                    .fileExt(ext)
//                    .contentType(multipartFile.getContentType())
//                    .build();
//            restImages.add(restImageRepository.save(restImage));
//        }
//        return restImages;
//    }
}

