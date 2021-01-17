package ohlim.fooda.error.exception;

import ohlim.fooda.error.ErrorCode;

public class FolderNotEmptyException extends CustomException {
    private static final long serialVersionUID = -2116671122895194101L;

    public FolderNotEmptyException(){
        super(ErrorCode.FOLDER_NOT_EMPTY);
    }

}
