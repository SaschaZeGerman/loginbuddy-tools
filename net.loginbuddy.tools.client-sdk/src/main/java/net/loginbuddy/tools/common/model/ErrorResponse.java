package net.loginbuddy.tools.common.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class ErrorResponse {

    private String error;
    private String errorDescription;

    public ErrorResponse(String error, String errorDescription) {
        this.error = error;
        this.errorDescription = errorDescription;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getUrlEncodedError() {
        try {
            return String.format("error=%s&error_description=%s",
                    URLEncoder.encode(error, "UTF-8"),
                    URLEncoder.encode(errorDescription, "UTF-8")
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            // should never happen
            return null;
        }
    }
}
