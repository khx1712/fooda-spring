package ohlim.fooda.service;

import ohlim.fooda.domain.Folder;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.FolderDto.*;
import ohlim.fooda.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FolderService {

    private FolderRepository folderRepository;
    private RestaurantService restaurantService;

    @Autowired
    public FolderService(FolderRepository folderRepository, RestaurantService restaurantService){
        this.folderRepository = folderRepository;
        this.restaurantService = restaurantService;
    }

    public Folder addFolder(Folder folder) {
        return folderRepository.save(folder);
    }

    public Folder updateFolder(Long id, String userName, String name) throws FolderNotFoundException {
        Folder folder = folderRepository.findAllByIdAndUserName(id, userName)
                .orElseThrow(() -> new FolderNotFoundException(id));
        folder.setName(name);
        return folderRepository.save(folder);
    }

    public void deleteFolder(Long id, String userName) throws FolderNotFoundException {
        Folder folder = folderRepository.findAllByIdAndUserName(id, userName)
                .orElseThrow(() -> new FolderNotFoundException(id));
        folderRepository.delete(folder);
    }

    public List<Folder> getFolders(String username) {
        List<Folder> folders = folderRepository.findAllByUserName(username);
        return folders;
    }

    public Folder getFolder(Long id) throws FolderNotFoundException {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new FolderNotFoundException(id));
        return folder;
    }

    public List<FolderIncludeResInfo> getFoldersIncludeRes(String username) {
        List<Folder> folders = folderRepository.findAllByUserName(username);
        List<FolderIncludeResInfo> foldersRes = new ArrayList<>();
        for(Folder folder: folders){
            List<Restaurant> restaurants = restaurantService.getRestaurantByFolderId(folder.getId());
            FolderIncludeResInfo folderIncludeResInfo = FolderIncludeResInfo.builder()
                    .id(folder.getId())
                    .name(folder.getName())
                    .RestaurantCnt(restaurants.size())
                    .restaurants(restaurants)
                    .build();
            foldersRes.add(folderIncludeResInfo);
        }
        return foldersRes;
    }

    public FolderIncludeResInfo getFolderIncludeRes(String username, Long id) throws FolderNotFoundException {
        Folder folder = folderRepository.findAllByIdAndUserName(id, username)
                .orElseThrow(()-> new FolderNotFoundException(id));
        List<FolderIncludeResInfo> foldersRes = new ArrayList<>();
        List<Restaurant> restaurants = restaurantService.getRestaurantByFolderId(folder.getId());
        FolderIncludeResInfo folderIncludeResInfo = FolderIncludeResInfo.builder()
                .id(folder.getId())
                .name(folder.getName())
                .RestaurantCnt(restaurants.size())
                .restaurants(restaurants)
                .build();
        return folderIncludeResInfo;
    }
}
