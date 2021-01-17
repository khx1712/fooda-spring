package ohlim.fooda.service;

import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Folder;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.folder.FolderDto;
import ohlim.fooda.dto.folder.FolderDetailDto;
import ohlim.fooda.dto.folder.FolderRestaurantDto;
import ohlim.fooda.dto.restaurant.RestaurantThumbnailDto;
import ohlim.fooda.error.exception.AccountNotFoundException;
import ohlim.fooda.error.exception.FolderNotEmptyException;
import ohlim.fooda.error.exception.FolderNotFoundException;
import ohlim.fooda.repository.AccountRepository;
import ohlim.fooda.repository.FolderRepository;
import ohlim.fooda.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FolderService {

    private FolderRepository folderRepository;
    private AccountRepository accountRepository;
    private RestaurantRepository restaurantRepository;
    private RestaurantService restaurantService;

    @Autowired
    public FolderService(FolderRepository folderRepository, RestaurantService restaurantService
            , AccountRepository accountRepository, RestaurantRepository restaurantRepository){
        this.restaurantRepository = restaurantRepository;
        this.folderRepository = folderRepository;
        this.restaurantService = restaurantService;
        this.accountRepository = accountRepository;
    }

    public Long addFolder(String userName, FolderDto resource) {
        Account account = accountRepository.findByUserName(userName).orElseThrow(()-> new AccountNotFoundException());
        Folder folder = Folder.createFolder(account, resource);
        folderRepository.save(folder);
        return folder.getId();
    }

    public void updateFolder(Long id, FolderDto folderDto) throws FolderNotFoundException {
        Folder folder = folderRepository.getFolder(id);
        folder.setName(folder.getName());
    }

    // TODO : 폴더를 삭제하면 하위의 모든 식당을 삭제할 것인지 정하기 (지금은 폴더안에 식당 있으면 삭제 못하게 하기)
    public void deleteFolder(Long id, String userName) throws FolderNotFoundException {
        List<Restaurant> restaurants = restaurantRepository.findByFolderId(id);
        if(!restaurants.isEmpty()){
            throw new FolderNotEmptyException();
        }
        Folder folder = folderRepository.findByIdAndUserName(id, userName);
        folderRepository.delete(folder);
    }

    public List<FolderDetailDto> getFoldersByUserName(String username) {
        List<Folder> folders = folderRepository.findByUserName(username);
        List<FolderDetailDto> folderDetailDtos = new ArrayList<>();
        for(Folder folder: folders){
            folderDetailDtos.add(FolderDetailDto.createFolderDetailDto(folder));
        }
        return folderDetailDtos;
    }

    public FolderDetailDto getFolder(Long id) throws FolderNotFoundException {
        Folder folder = folderRepository.getFolder(id);
        return FolderDetailDto.createFolderDetailDto(folder);
    }

    public FolderRestaurantDto getFolderIncludeRest(Long id) {
        Folder folder = folderRepository.getFolderRestaurant(id);
        return FolderRestaurantDto.createFolderRestaurantFto(folder);
    }

//    public FolderIncludeRestDto getFolderIncludeRes(String username, Long id) throws FolderNotFoundException {
//        Folder folder = folderRepository.findAllByIdAndUserName(id, username)
//                .orElseThrow(()-> new FolderNotFoundException());
//        List<FolderIncludeRestDto> foldersRes = new ArrayList<>();
//        List<Restaurant> restaurants = restaurantService.getRestaurantByFolderId(folder.getId());
//        FolderIncludeRestDto folderIncludeResInfo = FolderIncludeRestDto.builder()
//                .id(folder.getId())
//                .name(folder.getName())
//                .RestaurantCnt(restaurants.size())
//                .restaurants(restaurants)
//                .build();
//        return folderIncludeResInfo;
//    }
}
