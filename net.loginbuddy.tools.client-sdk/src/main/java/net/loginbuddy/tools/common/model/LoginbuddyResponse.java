package net.loginbuddy.tools.common.model;

import org.json.simple.JSONObject;

public class LoginbuddyResponse {

    private String state;
    private int status;
    private final JSONObject completeResponse;

    public LoginbuddyResponse(String state, int status, JSONObject completeResponse) {
        this.state = state;
        this.status = status;
        this.completeResponse = completeResponse;
    }

    /**
     * @return The content of 'details_loginbuddy' of the complete response
     */
    public LoginbuddyDetails getLoginbuddyDetails() {
        LoginbuddyDetails ld = new LoginbuddyDetails();
        JSONObject jo = getDetails("details_loginbuddy");
        ld.setAud((String) jo.get("aud"));
        ld.setIss((String) jo.get("iss"));
        ld.setNonce((String) jo.get("nonce"));
        ld.setIat((long) jo.get("iat"));
        return ld;
    }

    /**
     * @return The OAuth / OIDC standard values of the complete response
     */
    public OAuthDetails getOAuthDetails() {
        OAuthDetails response = new OAuthDetails();
        response.setAccessToken((String) completeResponse.get("access_token"));
        response.setRefreshToken((String) completeResponse.get("refresh_token"));
        response.setScope((String) completeResponse.get("scope"));
        response.setExpiresIn(Integer.parseInt(String.valueOf(completeResponse.get("expires_in"))));
        response.setTokenType((String) completeResponse.get("token_type"));
        response.setIdToken((String) completeResponse.get("id_token"));
        return response;
    }

    /**
     * @return The content of 'details_provider' of the complete response
     */
    public ProviderDetails getProviderDetails() {
        ProviderDetails pd = new ProviderDetails();
        pd.setDetails(getDetails("details_provider"));
        return pd;
    }

    /**
     * @return The content of 'details_normalized' of the complete response
     */
    public JSONObject getNormalizedDetails() {
        return getDetails("details_normalized");
    }

    public ErrorResponse getError() {
        if (completeResponse.get("error") != null) {
            return new ErrorResponse(status, (String) completeResponse.get("error"), (String) completeResponse.get("error_description")
            );
        }
        return null;
    }

    private JSONObject getDetails(String details) {
        return (JSONObject) completeResponse.get(details);
    }

    public String getState() {
        return state;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return completeResponse.toJSONString();
    }
}