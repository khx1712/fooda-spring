package ohlim.fooda.service;

import ohlim.fooda.domain.Folder;
import ohlim.fooda.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

    private FolderRepository folderRepository;

    @Autowired
    public FolderService(FolderRepository folderRepository){
        this.folderRepository = folderRepository;
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
}
