package net.loginbuddy.tools.common.model;

import org.json.simple.JSONObject;

public class ProviderDetails {

    private String provider;
    private JSONObject details;

    public String getProvider() {
        return provider;
    }

    public JSONObject getDetails() {
        return details;
    }

    public void setDetails(JSONObject details) {
        this.provider = (String)details.get("provider");
        this.details = details;
    }
}
