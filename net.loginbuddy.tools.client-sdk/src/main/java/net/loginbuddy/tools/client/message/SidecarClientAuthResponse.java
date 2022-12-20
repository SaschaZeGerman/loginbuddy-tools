package net.loginbuddy.tools.client.message;

import net.loginbuddy.tools.client.SidecarClient;
import org.apache.http.client.HttpClient;

public class SidecarClientAuthResponse {

    private final SidecarClient client;

    private String queryString;

    /**
     * Create a new AuthorizationRequest
     *
     * @param client      Must not be null
     * @param queryString Must not be null
     */
    public SidecarClientAuthResponse(SidecarClient client, String queryString) {
        this.client = client;
        this.queryString = queryString;

    }

    public SidecarClient build() {
        return client;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public SidecarClientAuthResponse setHttpClient(HttpClient httpClient) {
        this.client.setHttpClient(httpClient);
        return this;
    }
}
