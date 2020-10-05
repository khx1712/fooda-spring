package ohlim.fooda.service;

import ohlim.fooda.domain.Folder;

public class FolderNotFoundException extends Throwable {
    FolderNotFoundException(Long id){
        super("Folder not found: " + id);
    }
}
