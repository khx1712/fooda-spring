package ohlim.fooda.error.exception;

import ohlim.fooda.error.ErrorCode;

public class DuplicateEmailException extends CustomException {

    private static final long serialVersionUID = -2116671122895194101L;

    public DuplicateEmailException(){
        super(ErrorCode.DUPLICATE_EMAIL);
    }
}