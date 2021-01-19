package ohlim.fooda.error;


public enum ErrorCode {
    INVALID_PARAMETER(400, null, "Invalid Request Data"),
    RESTAURANT_NOT_FOUND(401, "C001", "Restaurant Not Found"),
    REST_IMAGE_NOT_FOUND(402, "C002", "RestImage Not Found"),
    ACCOUNT_NOT_FOUND(402, "C005", "Account Not Found"),
    FOLDER_NOT_FOUND(402, "C003", "Folder Not Found"),
    FOLDER_NOT_EMPTY(402, "C005", "Folder Not Empty"),
    INVALID_LOCATION(402, "C004", "Invalid Location"),
    DUPLICATE_USERNAME(402, "C006", "Duplicate UserName"),
    DUPLICATE_EMAIL(402, "C006", "Duplicate Email"),
    NON_LOGIN(402, "D001", "Non Login"),
    INVALID_TOKEN(402, "D002","Invalid Token"),
    EXPIRED_ACCESS_TOKEN(402, "D003", "Expired AccessToken"),
    ACCESS_DENIED(402, "D004", "Access Denied"),
    EXPIRED_REFRESH_TOKEN(402, "C0010", "Expired RefreshToken");

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
