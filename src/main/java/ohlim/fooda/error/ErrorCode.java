package ohlim.fooda.error;


public enum ErrorCode {
    INVALID_PARAMETER(400, null, "Invalid Request Data"),
    RESTAURANT_NOT_FOUND(406, "C001", "Restaurant Not Found"),
    REST_IMAGE_NOT_FOUND(406, "C002", "RestImage Not Found"),
    ACCOUNT_NOT_FOUND(406, "C003", "Account Not Found"),
    FOLDER_NOT_FOUND(406, "C004", "Folder Not Found"),
    FOLDER_NOT_EMPTY(409, "C005", "Folder Not Empty"),
    INVALID_LOCATION(406, "C006", "Invalid Location"),
    DUPLICATE_USERNAME(400, "C007", "Duplicate UserName"),
    DUPLICATE_EMAIL(400, "C008", "Duplicate Email"),
    NON_LOGIN(401, "D001", "Non Login"),
    INVALID_ACCESS_TOKEN(401, "D002","Invalid Access Token"),
    EXPIRED_ACCESS_TOKEN(401, "D003", "Expired AccessToken"),
    ACCESS_DENIED(403, "D004", "Access Denied"),
    INVALID_REFRESH_TOKEN(401, "C009","Invalid Access Token"),
    EXPIRED_REFRESH_TOKEN(401, "C010", "Expired RefreshToken"),
    NO_REFRESH_TOKEN(401, "C011", "No Refresh Token");

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
