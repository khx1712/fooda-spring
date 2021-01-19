package ohlim.fooda.error.exception;

import ohlim.fooda.error.ErrorCode;

public class InvalidRefreshTokenException extends CustomException {
    private static final long serialVersionUID = -2116671122895194101L;

    public InvalidRefreshTokenException() {
        super(ErrorCode.INVALID_REFRESH_TOKEN);
    }
}
