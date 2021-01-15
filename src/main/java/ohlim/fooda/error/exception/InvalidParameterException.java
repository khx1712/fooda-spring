package ohlim.fooda.error.exception;

import ohlim.fooda.error.ErrorCode;
import ohlim.fooda.error.exception.CustomException;
import org.springframework.validation.Errors;

public class InvalidParameterException extends CustomException {
    private final Errors errors;

    public InvalidParameterException(Errors errors) {
        super(ErrorCode.INVALID_PARAMETER);
        this.errors = errors;
    }
    public Errors getErrors() {
        return this.errors;
    }
}
