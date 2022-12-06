package net.loginbuddy.tools.common.exception;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class LoginbuddyToolsException extends Exception {

    private static final Logger LOGGER = Logger.getLogger(String.valueOf(LoginbuddyToolsException.class));

    private String error, errorDescription;
    private int httpStatus;

    public LoginbuddyToolsException(String error, String errorDescription) {
        this(error, errorDescription, -1);
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
