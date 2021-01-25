package ohlim.fooda.error.exception;

import ohlim.fooda.error.ErrorCode;

public class DefaultFolderException extends CustomException {

    private static final long serialVersionUID = -2116671122895194101L;

    public DefaultFolderException(){
        super(ErrorCode.DEFAULT_FOLDER);
    }
}