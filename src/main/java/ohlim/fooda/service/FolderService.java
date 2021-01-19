package ohlim.fooda.service;

import ohlim.fooda.domain.Account;
import ohlim.fooda.domain.Folder;
import ohlim.fooda.domain.Restaurant;
import ohlim.fooda.dto.folder.FolderDto;
import ohlim.fooda.dto.folder.FolderDetailDto;
import ohlim.fooda.dto.folder.FolderRestaurantDto;
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

    /**
     * 폴더정보를 받아 폴더 저장, 해당하는 유저에 객체 참조합니다.
     * @param userName 폴더를 가지는 유저 아이디
     * @param folderDto 폴더 정보
     * @return 저장된 folder_id
     */
    public Long addFolder(String userName, FolderDto folderDto) {
        Account account = accountRepository.findByUserName(userName).orElseThrow(()-> new AccountNotFoundException());
        Folder folder = Folder.createFolder(account, folderDto);
        folderRepository.save(folder);
        return folder.getId();
    }

    /**
     * 폴더정보를 받아 폴더 수정합니다.
     * @param id 수정하는 폴더 아이디
     * @param folderDto 폴더 정보
     */
    public void updateFolder(Long id, FolderDto folderDto) throws FolderNotFoundException {
        Folder folder = folderRepository.getFolder(id);
        folder.setName(folder.getName());
    }

    /**
     * 폴더 아이디를 받아 폴더를 삭제합니다.
     * @param id 삭제하는 폴더 아이디
     * @param userName 폴더 정보
     */
    // TODO : 폴더를 삭제하면 하위의 모든 식당을 삭제할 것인지 정하기 (지금은 폴더안에 식당 있으면 삭제 못하게 하기)
    public void deleteFolder(Long id, String userName) throws FolderNotFoundException {
        List<Restaurant> restaurants = restaurantRepository.findByFolderId(id);
        if(!restaurants.isEmpty()){
            throw new FolderNotEmptyException();
        }
        Folder folder = folderRepository.findByIdAndUserName(id, userName);
        folderRepository.delete(folder);
    }

    /**
     * 유저 아이디에 해당하는 폴더 상세 목록을 반환합니다.
     * @param username 유저 아이디
     * @return 유저 아이디에 해당하는 폴더 상세 목록
     */
    public List<FolderDetailDto> getFoldersByUserName(String username) {
        List<Folder> folders = folderRepository.findByUserName(username);
        List<FolderDetailDto> folderDetailDtos = new ArrayList<>();
        for(Folder folder: folders){
            folderDetailDtos.add(FolderDetailDto.createFolderDetailDto(folder));
        }
        return folderDetailDtos;
    }

    /**
     * 폴더 아이디에 해당하는 폴더 상세를 반환합니다.
     * @param id 폴더 아이디
     * @return 폴더 아이디에 해당하는 폴더 상세
     */
    public FolderDetailDto getFolder(Long id) throws FolderNotFoundException {
        Folder folder = folderRepository.getFolder(id);
        return FolderDetailDto.createFolderDetailDto(folder);
    }

    /**
     * 폴더 아이디에 해당하는 폴더 상세를 식당들을 포함하여 반환합니다.
     * @param id 폴더 아이디
     * @return 폴더 아이디에 해당하는 폴더 상세(식당 목록 포함)
     */
    public FolderRestaurantDto getFolderIncludeRest(Long id) {
        Folder folder = folderRepository.getFolderRestaurant(id);
        return FolderRestaurantDto.createFolderRestaurantFto(folder);
    }
}
