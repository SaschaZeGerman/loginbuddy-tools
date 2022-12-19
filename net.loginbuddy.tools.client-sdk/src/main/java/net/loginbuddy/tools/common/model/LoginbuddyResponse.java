package net.loginbuddy.tools.common.model;

import org.json.simple.JSONObject;

public class LoginbuddyResponse {

    private String state;
    private int status;
    private final JSONObject providerResponse;

    public LoginbuddyResponse(String state, int status, JSONObject providerResponse) {
        this.state = state;
        this.status = status;
        this.providerResponse = providerResponse;
    }

    public LoginbuddyDetails getLoginbuddyDetails() {
        LoginbuddyDetails ld = new LoginbuddyDetails();
        JSONObject jo = getDetails("details_loginbuddy");
        ld.setAud((String) jo.get("aud"));
        ld.setIss((String) jo.get("iss"));
        ld.setNonce((String) jo.get("nonce"));
        ld.setIat((long) jo.get("iat"));
        return ld;
    }

    public OAuthDetails getOAuthDetails() {
        OAuthDetails response = new OAuthDetails();
        response.setAccessToken((String) providerResponse.get("access_token"));
        response.setRefreshToken((String) providerResponse.get("refresh_token"));
        response.setScope((String) providerResponse.get("scope"));
        response.setExpiresIn((Long)providerResponse.get("expires_in"));
        response.setTokenType((String) providerResponse.get("token_type"));
        response.setIdToken((String) providerResponse.get("id_token"));
        return response;
    }

    public JSONObject getProviderDetails() {
        return getDetails("details_provider");
    }

    public JSONObject getNormalizedDetails() {
        return getDetails("details_normalized");
    }

    public ErrorResponse getError() {
        if (providerResponse.get("error") != null) {
            return new ErrorResponse(status, (String) providerResponse.get("error"), (String) providerResponse.get("error_description")
            );
        }
        return null;
    }

    private JSONObject getDetails(String details) {
        return (JSONObject) providerResponse.get(details);
    }

    public String getState() {
        return state;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return providerResponse.toJSONString();
    }
}