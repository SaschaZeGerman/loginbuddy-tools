package net.loginbuddy.tools.client.message;

import net.loginbuddy.tools.client.SidecarClient;
import net.loginbuddy.tools.common.oidc.Prompt;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.*;

public class SidecarClientAuthRequest implements ParameterProvider {

    private final SidecarClient client;

    private final Map<String, BasicNameValuePair> formParameters;

    /**
     * Create a new AuthorizationRequest
     *
     * @param client   Must not be null
     * @param provider Must not be null
     */
    public SidecarClientAuthRequest(SidecarClient client, String provider) {
        this.client = client;
        formParameters = new HashMap<>();
        formParameters.put("provider", new BasicNameValuePair("provider", provider));
        formParameters.put("target_path", new BasicNameValuePair("target_path", client.getLoginbuddyAuthorizePath()));
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
        return new ArrayList<>(formParameters.values());
    }

    /**
     * Optional: this will be returned, as is, after Loginbuddy has received a token response from a provider. Use this to manage the clients session state.
     *
     * @param state
     * @return
     */
    public SidecarClientAuthRequest setState(String state) {
        formParameters.put("state", new BasicNameValuePair("state", state));
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
        formParameters.put("scope", new BasicNameValuePair("scope", scope));
        return this;
    }

    /**
     * Optional: Set the nonce value for the authorization request. If not set Loginbuddy will create one.
     *
     * @param nonce
     * @return
     */
    public SidecarClientAuthRequest setNonce(String nonce) {
        formParameters.put("nonce", new BasicNameValuePair("nonce", nonce));
        return this;
    }

    /**
     * See {@link #setDynamicProvider(String, String)}
     *
     * @param issuer
     * @return
     */
    public SidecarClientAuthRequest setDynamicProvider(String issuer) {
        return setDynamicProvider(issuer, null);
    }

    /**
     * Optional: To use a provider via dynamic registration. Loginbuddy will register itself as a client. The 'provider' will be set automatically
     *
     * @param issuer
     * @param discoveryUrl
     * @return
     */
    public SidecarClientAuthRequest setDynamicProvider(String issuer, String discoveryUrl) {
        formParameters.put("issuer", new BasicNameValuePair("issuer", issuer));
        if (discoveryUrl != null) {
            formParameters.put("discovery_url", new BasicNameValuePair("discovery_url", discoveryUrl));
        }
        formParameters.put("provider", new BasicNameValuePair("provider", "")); // needs to be empty to enable dynamic registration
        formParameters.put("accept_dynamic_provider", new BasicNameValuePair("accept_dynamic_provider", "true"));
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
        formParameters.put("prompt", new BasicNameValuePair("prompt", prompt.toString()));
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
        formParameters.put("login_hint", new BasicNameValuePair("login_hint", loginHint));
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
        formParameters.put("id_token_hint", new BasicNameValuePair("id_token_hint", idTokenHint));
        return this;
    }

    /**
     * If set your client receives a JWT (signed by loginbuddy-sidecar) as response instead of a JSON document.
     * The JWT will be signed using alg=RS256.
     *
     * @return
     */
    public SidecarClientAuthRequest setSignedResponseAlgRS256() {
        formParameters.put("signed_response_alg", new BasicNameValuePair("signed_response_alg", "RS256"));
        return this;
    }

    /**
     * If set your client receives a JWT (signed by loginbuddy-sidecar) as response instead of a JSON document.
     * The JWT will be signed using alg=ES256.
     *
     * @return
     */
    public SidecarClientAuthRequest setSignedResponseAlgES256() {
        formParameters.put("signed_response_alg", new BasicNameValuePair("signed_response_alg", "ES256"));
        return this;
    }

    /**
     * Overrides the default URLs that point to Loginbuddy
     * @param path Setting the target, i.e.: .../authorize
     * @return
     */
    public SidecarClientAuthRequest setTargetPath(String path) {
        formParameters.put("target_path", new BasicNameValuePair("target_path", path));
        return this;
    }

    public SidecarClientAuthRequest setClientId(String clientId) {
        formParameters.put("client_id", new BasicNameValuePair("client_id", clientId));
        return this;
    }
    public SidecarClientAuthRequest setClientSecret(String clientSecret) {
        formParameters.put("client_secret", new BasicNameValuePair("client_secret", clientSecret));
        return this;
    }
    public SidecarClientAuthRequest setResponseType(String responseType) {
        formParameters.put("response_type", new BasicNameValuePair("response_type", responseType));
        return this;
    }

    /**
     * If set the providers access_token and refresh_token will be obfuscated before returned to your application.
     * If not set the original values will be passed back to your application.
     *
     * @return
     */
    public SidecarClientAuthRequest setObfuscateToken() {
        formParameters.put("obfuscate_token", new BasicNameValuePair("obfuscate_token", "true"));
        return this;
    }

    /**
     * Configure a http client. This, however, is intended for testing purposes and should be ignored otherwise
     * @param httpClient
     */
    public SidecarClientAuthRequest setHttpClient(HttpClient httpClient) {
        this.client.setHttpClient(httpClient);
        return this;
    }
}
