package ohlim.fooda.error.exception;

import ohlim.fooda.error.ErrorCode;

public class ExpiredRefreshTokenException extends CustomException {
    private static final long serialVersionUID = -2116671122895194101L;

    public ExpiredRefreshTokenException() {
        super(ErrorCode.EXPIRED_REFRESH_TOKEN);
    }
}

