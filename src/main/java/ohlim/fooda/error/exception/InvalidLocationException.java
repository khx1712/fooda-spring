package ohlim.fooda.error.exception;

import ohlim.fooda.error.ErrorCode;
import ohlim.fooda.error.exception.CustomException;

public class InvalidLocationException extends CustomException {
    private static final long serialVersionUID = -2116671122895194101L;

    public InvalidLocationException() {
        super(ErrorCode.INVALID_LOCATION);
    }
}
