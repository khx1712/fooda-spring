package ohlim.fooda.error.exception;

import ohlim.fooda.error.ErrorCode;

public class AccountNotFoundException extends CustomException {

    private static final long serialVersionUID = -2116671122895194101L;

    public AccountNotFoundException(){
        super(ErrorCode.ACCOUNT_NOT_FOUND);
    }
}
