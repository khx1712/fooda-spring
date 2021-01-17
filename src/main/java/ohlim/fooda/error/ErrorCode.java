package ohlim.fooda.error;


public enum ErrorCode {
    INVALID_PARAMETER(400, null, "Invalid Request Data"),
    RESTAURANT_NOT_FOUND(401, "C001", "Restaurant Not Found"),
    REST_IMAGE_NOT_FOUND(402, "C002", "RestImage Not Found"),
    ACCOUNT_NOT_FOUND(402, "C005", "Account Not Found"),
    FOLDER_NOT_FOUND(402, "C003", "Folder Not Found"),
    FOLDER_NOT_EMPTY(402, "C005", "Folder Not Empty"),
    INVALID_LOCATION(402, "C004", "Invalid Location");

    private final String code;
    private final String message;
    private final int status;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
