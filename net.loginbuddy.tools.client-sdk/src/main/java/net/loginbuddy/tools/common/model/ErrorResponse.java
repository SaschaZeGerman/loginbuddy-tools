package net.loginbuddy.tools.common.model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
        return String.format("error=%s&error_description=%s",
                URLEncoder.encode(error, StandardCharsets.UTF_8),
                URLEncoder.encode(errorDescription, StandardCharsets.UTF_8)
        );
    }
}
