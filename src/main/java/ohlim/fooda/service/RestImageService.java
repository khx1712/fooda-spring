package ohlim.fooda.service;

import ohlim.fooda.domain.RestImage;
import ohlim.fooda.repository.RestImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class RestImageService {

    RestImageRepository restImageRepository;

    @Autowired
    public RestImageService(RestImageRepository restImageRepository) {
        this.restImageRepository = restImageRepository;
    }

    public RestImage fileUpload(MultipartFile multipartFile, String userName, Long restaurantId) throws IOException {
        String fileOriginName = multipartFile.getOriginalFilename();
        String contestType = multipartFile.getContentType();
        String[] nameArry = fileOriginName.split("\\.");
        String ext = nameArry[nameArry.length-1];
        String filePath = FileHandler.getFileUploadPath();
        String fileSaveName = FileHandler.getFileSaveName(ext, userName);

        File dest = new File(filePath + "/" + fileSaveName);
        multipartFile.transferTo(dest);
        RestImage restImage = RestImage.builder()
                .fileOriginName(fileOriginName)
                .userName(userName)
                .restaurantId(restaurantId)
                .filePath(filePath + "/" + fileSaveName)
                .fileSaveName(fileSaveName)
                .fileExt(ext)
                .contentType(multipartFile.getContentType())
                .build();

        return restImageRepository.save(restImage);
    }
}

