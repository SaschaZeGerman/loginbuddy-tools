package net.loginbuddy.tools.common.exception;

public class LoginbuddyToolsException extends Exception {

    private final String error;
    private final String errorDescription;
    private final int httpStatus;

    public LoginbuddyToolsException(String error, String errorDescription) {
        this(error, errorDescription, 400);
    }

    public LoginbuddyToolsException(String error, String errorDescription, int httpStatus) {
        this.error = error;
        this.errorDescription = errorDescription;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return String.format("error=%s, error_description=%s", error, errorDescription);
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
