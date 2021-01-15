package ohlim.fooda.error.exception;

import ohlim.fooda.error.ErrorCode;
import ohlim.fooda.error.exception.CustomException;

public class RestImageNotFoundException extends CustomException {

    private static final long serialVersionUID = -2116671122895194101L;

    public RestImageNotFoundException(){
        super(ErrorCode.REST_IMAGE_NOT_FOUND);
    }
}
