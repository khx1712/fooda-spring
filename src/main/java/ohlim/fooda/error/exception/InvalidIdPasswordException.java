package ohlim.fooda.error.exception;

import ohlim.fooda.error.ErrorCode;

public class InvalidIdPasswordException extends CustomException {
    private static final long serialVersionUID = -2116671122895194101L;

    public InvalidIdPasswordException() {
        super(ErrorCode.INVALID_ID_PASSWORD);
    }
}