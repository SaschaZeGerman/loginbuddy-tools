package net.loginbuddy.tools.common.exception;

import java.net.URLDecoder;
import java.util.logging.Logger;

public class LoginbuddyToolsException extends Exception {

    private static final Logger LOGGER = Logger.getLogger(String.valueOf(LoginbuddyToolsException.class));

    private String error, errorDescription;

    public LoginbuddyToolsException(Throwable cause) {
        super(cause);
    }

    /**
     *
     * @param message Follows this pattern: https://hostname:port/?error=anerror&error_decription=adescription
     */
    public LoginbuddyToolsException(String message) {
        try {
            error = URLDecoder.decode(message.split("[?]")[1].split("[&]")[0].split("[=]")[1], "UTF-8");
            errorDescription = URLDecoder.decode(message.split("[?]")[1].split("[&]")[1].split("[=]")[1], "UTF-8");
        } catch(Exception e) {
            LOGGER.warning(e.getMessage());
            error = "unknown";
            errorDescription="unknown error_description. Please check the logs";
        }
    }

    public LoginbuddyToolsException(String error, String errorDescription) {
        this.error = error;
        this.errorDescription = errorDescription;
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
}
