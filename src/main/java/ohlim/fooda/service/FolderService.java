package ohlim.fooda.service;

import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Folder;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.folder.FolderIncludeRestDto;
import ohlim.fooda.error.exception.AccountNotFoundException;
import ohlim.fooda.error.exception.FolderNotFoundException;
import ohlim.fooda.repository.AccountRepository;
import ohlim.fooda.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FolderService {

    private FolderRepository folderRepository;
    private AccountRepository accountRepository;
    private RestaurantService restaurantService;

    @Autowired
    public FolderService(FolderRepository folderRepository, RestaurantService restaurantService, AccountRepository accountRepository){
        this.folderRepository = folderRepository;
        this.restaurantService = restaurantService;
        this.accountRepository = accountRepository;
    }

    public Folder addFolder(Folder folder) {
        return folderRepository.save(folder);
    }

    public Folder updateFolder(Long id, String userName, String name) throws FolderNotFoundException {
        Account account = accountRepository.findByUserName(userName)
                .orElseThrow(() -> new AccountNotFoundException());
        Folder folder = folderRepository.findAllByIdAndAccount(id, account)
                .orElseThrow(() -> new FolderNotFoundException());
        folder.setName(name);
        return folderRepository.save(folder);
    }

    public void deleteFolder(Long id, String userName) throws FolderNotFoundException {
        Account account = accountRepository.findByUserName(userName)
                .orElseThrow(() -> new AccountNotFoundException());
        Folder folder = folderRepository.findAllByIdAndAccount(id, account)
                .orElseThrow(() -> new FolderNotFoundException());
        folderRepository.delete(folder);
    }

    public List<Folder> getFolders(String username) {
        Account account = accountRepository.findByUserName(username)
                .orElseThrow(() -> new AccountNotFoundException());
        List<Folder> folders = folderRepository.findAllByAccount(account);
        return folders;
    }

    public Folder getFolder(Long id) throws FolderNotFoundException {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new FolderNotFoundException());
        return folder;
    }

//    public List<FolderIncludeRestDto> getFoldersIncludeRes(String userName) {
//        Account account = accountRepository.findByUserName(userName)
//                .orElseThrow(() -> new AccountNotFoundException());
//        List<Folder> folders = folderRepository.findAllByAccount(account);
//        List<FolderIncludeRestDto> foldersRes = new ArrayList<>();
//        for(Folder folder: folders){
//            List<Restaurant> restaurants = restaurantService.getRestaurantByFolderId(folder.getId());
//            FolderIncludeRestDto folderIncludeRestDTO = FolderIncludeRestDto.builder()
//                    .id(folder.getId())
//                    .name(folder.getName())
//                    .RestaurantCnt(restaurants.size())
//                    .restaurants(restaurants)
//                    .build();
//            foldersRes.add(folderIncludeRestDTO);
//        }
//        return foldersRes;
//    }

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
