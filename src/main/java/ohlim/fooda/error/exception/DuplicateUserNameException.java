package ohlim.fooda.error.exception;

import ohlim.fooda.error.ErrorCode;

public class DuplicateUserNameException extends CustomException {

    private static final long serialVersionUID = -2116671122895194101L;

    public DuplicateUserNameException(){
        super(ErrorCode.DUPLICATE_USERNAME);
    }
}
