package net.loginbuddy.tools.client.message;

import net.loginbuddy.tools.client.SidecarClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class SidecarClientRefreshTokenRequest implements ParameterProvider {

    private final SidecarClient client;

    private final List<NameValuePair> formParameters;

    /**
     * Create a new AuthorizationRequest
     *
     * @param client   Must not be null
     * @param refreshToken Must not be null
     */
    public SidecarClientRefreshTokenRequest(SidecarClient client, String refreshToken) {
        this.client = client;
        formParameters = new ArrayList<>();
        formParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
        formParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
    }

    /**
     * Call this method once all parameters are set.
     *
     * @return
     */
    public SidecarClient build() {
        return client;
    }

    @Override
    public List<NameValuePair> getParameters() {
        return formParameters;
    }

    /**
     * Optional: Set the SCOPE which should be included in the refresh_token request. Any given value has to match what has been configured in Loginbuddys configuration for the current provider.
     * If it is not set the configured scope for the given provider will be used.
     *
     * @param scope A space separated list of values. The value will be url-encoded, so they have to be in plain text
     * @return
     */
    public SidecarClientRefreshTokenRequest setScope(String scope) {
        formParameters.add(new BasicNameValuePair("scope", scope));
        return this;
    }

    /**
     * Configure a http client. This, however, is intended for testing purposes and should be ignored otherwise
     * @param httpClient
     */
    public SidecarClientRefreshTokenRequest setHttpClient(HttpClient httpClient) {
        this.client.setHttpClient(httpClient);
        return this;
    }
}
