package net.loginbuddy.tools.client.message;

import net.loginbuddy.tools.client.SidecarClient;
import net.loginbuddy.tools.common.oidc.Prompt;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class SidecarClientAuthRequest {

    private final SidecarClient client;

    private final List<NameValuePair> formParameters;

    /**
     * Create a new AuthorizationRequest
     *
     * @param client   Must not be null
     * @param provider Must not be null
     */
    public SidecarClientAuthRequest(SidecarClient client, String provider) {
        this.client = client;
        formParameters = new ArrayList<>();
        formParameters.add(new BasicNameValuePair("provider", provider));
    }

    /**
     * Call this method once all parameters are set.
     *
     * @return
     */
    public SidecarClient build() {
        return client;
    }

    public List<NameValuePair> getParameters() {
        return formParameters;
    }

    /**
     * Optional: this will be returned, as is, after Loginbuddy has received a token response from a provider. Use this to manage the clients session state.
     *
     * @param state
     * @return
     */
    public SidecarClientAuthRequest setState(String state) {
        formParameters.add(new BasicNameValuePair("state", state));
        return this;
    }

    /**
     * Optional: Set the SCOPE which should be included in the authorization request URL. Any given value has to match what has been configured in Loginbuddys configuration for the current provider.
     * If it is not set the configured scope for the given provider will be used.
     *
     * @param scope
     * @return
     */
    public SidecarClientAuthRequest setScope(String scope) {
        formParameters.add(new BasicNameValuePair("scope", scope));
        return this;
    }

    /**
     * Optional: Set the nonce value for the authorization request. If not set Loginbuddy will create one.
     *
     * @param nonce
     * @return
     */
    public SidecarClientAuthRequest setNonce(String nonce) {
        formParameters.add(new BasicNameValuePair("nonce", nonce));
        return this;
    }

    public SidecarClientAuthRequest setDynamicProvider(String issuer) {
        return setDynamicProvider(issuer, null);
    }

    public SidecarClientAuthRequest setDynamicProvider(String issuer, String discoveryUrl) {
        formParameters.add(new BasicNameValuePair("issuer", issuer));
        if (discoveryUrl != null) {
            formParameters.add(new BasicNameValuePair("discovery_url", discoveryUrl));
        }
        formParameters.add(new BasicNameValuePair("provider", "")); // needs to be empty to enable dynamic registration
        formParameters.add(new BasicNameValuePair("accept_dynamic_provider", "true"));
        return this;
    }

    /**
     * Optional: This value will be forwarded to the given provider as is. This parameter is taken out of the OpenID Connect core specification.
     * Loginbuddy will not include this parameter if it is not set.
     *
     * @param prompt
     * @return
     */
    public SidecarClientAuthRequest setPrompt(Prompt prompt) {
        formParameters.add(new BasicNameValuePair("prompt", prompt.toString()));
        return this;
    }


    /**
     * This value will be forwarded to the given provider as is. This parameter is taken out of the OpenID Connect core specification.
     * Loginbuddy will not include this parameter if it is not set.
     *
     * @param loginHint
     * @return
     */
    public SidecarClientAuthRequest setLoginHint(String loginHint) {
        formParameters.add(new BasicNameValuePair("login_hint", loginHint));
        return this;
    }

    /**
     * This value will be forwarded to the given provider as is. This parameter is taken out of the OpenID Connect core specification.
     * Loginbuddy will not include this parameter if it is not set.
     *
     * @param idTokenHint
     * @return
     */
    public SidecarClientAuthRequest setIdTokenHint(String idTokenHint) {
        formParameters.add(new BasicNameValuePair("id_token_hint", idTokenHint));
        return this;
    }

    /**
     * If set your client receives a JWT (signed by loginbuddy-sidecar) as response instead of a JSON document.
     * The JWT will be signed using alg=RS256.
     *
     * @return
     */
    public SidecarClientAuthRequest setSignedResponseAlgRS256() {
        formParameters.add(new BasicNameValuePair("signed_response_alg", "RS256"));
        return this;
    }

    /**
     * If set your client receives a JWT (signed by loginbuddy-sidecar) as response instead of a JSON document.
     * The JWT will be signed using alg=ES256.
     *
     * @return
     */
    public SidecarClientAuthRequest setSignedResponseAlgES256() {
        formParameters.add(new BasicNameValuePair("signed_response_alg", "ES256"));
        return this;
    }

    /**
     * If set the providers access_token and refresh_token will be obfuscated before returned to your application.
     * If not set the original values will be passed back to your application.
     *
     * @return
     */
    public SidecarClientAuthRequest setObfuscateToken() {
        formParameters.add(new BasicNameValuePair("obfuscate_token", "true"));
        return this;
    }

    public SidecarClientAuthRequest setHttpClient(HttpClient httpClient) {
        this.client.setHttpClient(httpClient);
        return this;
    }
}
