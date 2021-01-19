package ohlim.fooda.error.exception;

import ohlim.fooda.error.ErrorCode;

public class NoRefreshTokenException extends CustomException {
    private static final long serialVersionUID = -2116671122895194101L;

    public NoRefreshTokenException() {
        super(ErrorCode.NO_REFRESH_TOKEN);
    }
}
