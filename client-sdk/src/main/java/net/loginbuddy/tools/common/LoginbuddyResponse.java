package net.loginbuddy.tools.common;

import org.json.simple.JSONObject;

public class LoginbuddyResponse {

    private JSONObject providerResponse;
    private JSONObject detailsNormalized;

    public LoginbuddyResponse(JSONObject providerResponse) {
        this.providerResponse = providerResponse;
    }

    public JSONObject getDetailsNormalized() {
        return getDetails("details_normalized");
    }

    public JSONObject getDetailsLoginbuddy() {
        return getDetails("details_loginbuddy");
    }

    public JSONObject getDetailsProvider() {
        return getDetails("details_provider");
    }

    public JSONObject getOAuthResponse() {
        JSONObject response = new JSONObject();
        response.put("access_token", providerResponse.get("access_token"));
        if(providerResponse.get("refresh_token") != null) {
            response.put("refresh_token", providerResponse.get("refresh_token"));
        }
        if(providerResponse.get("scope") != null) {
            response.put("scope", providerResponse.get("scope"));
        }
        if(providerResponse.get("expires_in") != null) {
            response.put("expires_in", providerResponse.get("expires_in"));
        }
        if(providerResponse.get("token_type") != null) {
            response.put("token_type", providerResponse.get("token_type"));
        }
        if(providerResponse.get("id_token") != null) {
            response.put("id_token", providerResponse.get("id_token"));
        }
        return response;
    }

    public JSONObject getError() {
        if(providerResponse.get("error") != null) {
            return providerResponse;
        }
        return null;
    }

    private JSONObject getDetails(String details) {
        return (JSONObject) providerResponse.get(details);
    }
}