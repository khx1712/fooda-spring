package ohlim.fooda.error.exception;

import ohlim.fooda.error.ErrorCode;
import ohlim.fooda.error.exception.CustomException;

public class FolderNotFoundException extends CustomException {
    private static final long serialVersionUID = -2116671122895194101L;

    public FolderNotFoundException(){
        super(ErrorCode.FOLDER_NOT_FOUND);
    }
}
