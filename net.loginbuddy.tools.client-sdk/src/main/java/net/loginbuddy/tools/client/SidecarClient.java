package net.loginbuddy.tools.client;

import net.loginbuddy.tools.common.exception.LoginbuddyToolsException;
import net.loginbuddy.tools.common.model.LoginbuddyResponse;
import net.loginbuddy.tools.common.oidc.Prompt;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SidecarClient {

    private static final Logger LOGGER = Logger.getLogger(String.valueOf(SidecarClient.class));

    // http://localhost/?error={error}&error_description={errorDescription}
    private static Pattern pErrorUrl = Pattern.compile("[?]error=([a-zA-Z0-9-_ ]{0,48})[&]error_description=([a-zA-Z0-9-_ ]{0,128})");

    private SidecarClientAuthRequest authRequest;
    private SidecarClientAuthResponse authResponse;
    private String loginbuddyInitUrl;
    private String loginbuddyCallbackUrl;

    private HttpClient httpClient;

    private SidecarClient() {
        try {
            String loginbuddylocation = System.getenv("LOGINBUDDY_SIDECAR_LOCATION");
            if (loginbuddylocation == null) {
                LOGGER.info("Environment variable LOGINBUDDY_SIDECAR_LOCATION was not set, using default location: http://loginbuddy-sidecar:8044");
                loginbuddylocation = "http://loginbuddy-sidecar:8044";
            }
            loginbuddyInitUrl = loginbuddylocation.concat("/sidecar/initialize");
            loginbuddyCallbackUrl = loginbuddylocation.concat("/sidecar/callback?%s");
            LOGGER.info(String.format("ClientSDK connects to this location: %s", loginbuddylocation));
        } catch(RuntimeException e) {
            LOGGER.severe(String.format("Environment variable LOGINBUDDY_SIDECAR_LOCATION is not accessible, clientSDK is not usable: %s", e.getMessage()));
        }
    }

    /**
     * This method initiates the process of creating the authorization URL that may be used to redirect a user to its authorization endpoint.
     *
     * @param provider The provider for which to create the authorization Url. This value must match a 'provider' in Loginbuddy's config.json and must not be null!
     * @return
     */
    public static SidecarClientAuthRequest createAuthRequest(String provider) {
        SidecarClient client = new SidecarClient();
        client.authRequest = new SidecarClientAuthRequest(client, provider);
        return client.authRequest;
    }

    /**
     * The URL query component as received from the provider. Usually this will be '?code=aCode&state=aState' or '?error=anError&error_description=anErrorDescription'.
     * In either case it should be given to this method.
     *
     * @param queryString
     * @return
     */
    public static SidecarClientAuthResponse createAuthResponse(String queryString) {
        SidecarClient client = new SidecarClient();
        client.authResponse = new SidecarClientAuthResponse(client, queryString);
        return client.authResponse;
    }

    /**
     * This will return the providers oauth response and details that were added by Loginbuddy.
     *
     * @return
     * @throws Exception
     */
    public LoginbuddyResponse getAuthResponse() throws LoginbuddyToolsException {
        HttpGet authResultRequest = new HttpGet(String.format(loginbuddyCallbackUrl, this));
        HttpResponse authResultResponse = null;
        try {
            authResultResponse = getHttpClient().execute(authResultRequest);
            String authResponseString = EntityUtils.toString(authResultResponse.getEntity());
            return new LoginbuddyResponse(authResultResponse.getFirstHeader("X-State").getValue(), authResultResponse.getStatusLine().getStatusCode(), (JSONObject) new JSONParser().parse(authResponseString));
        } catch(Exception e) {
            throw new LoginbuddyToolsException("auth_response_error", e.getMessage(), authResultResponse == null ? 400 : authResultResponse.getStatusLine().getStatusCode());
        }
    }



    /**
     * After creating a SidecarClientAuthRequest this method returns the authorization URL that can be used to initiate the authorization request with the provider.
     * A NullPointerException will be thrown if the SidecarClientAuthRequest has not been created.
     *
     * @return The authorization URL
     */
    public String getAuthorizationUrl() throws LoginbuddyToolsException {

        HttpPost initAuthRequest = new HttpPost(loginbuddyInitUrl);
        HttpResponse initAuthResponse = null;
        try {
            initAuthRequest.setEntity(new UrlEncodedFormEntity(authRequest.getParameters()));

            // initialize the authorization flow. Loginbuddy will return the authorizationUrl that is valid for the selected provider
            initAuthResponse = getHttpClient().execute(initAuthRequest);

        } catch (Exception e) {
            LOGGER.warning(String.format("Loginbuddy could not be reached: %s", e.getMessage()));
            throw new LoginbuddyToolsException("connection_failed", e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }

        if (initAuthResponse.getStatusLine().getStatusCode() == 201) {
            // the location header contains the authorizationUrl for taking a browser to the target provider
            // the URL should be used as is
            return initAuthResponse.getHeaders("Location")[0].getValue();
        } else {
            // Error!
            // status=400 // or other than 201
            // Location=http://localhost/?error={error}&error_description={errorDescription}
            String errorUrl = URLDecoder.decode(initAuthResponse.getHeaders("Location")[0].getValue(), StandardCharsets.UTF_8);
            Matcher mErrorUrl = pErrorUrl.matcher(errorUrl);
            int httpStatus = initAuthResponse.getStatusLine().getStatusCode();
            if(mErrorUrl.find()) {
                throw new LoginbuddyToolsException(mErrorUrl.group(1), mErrorUrl.group(2), httpStatus);
            }
            LOGGER.warning(errorUrl);
            throw new LoginbuddyToolsException("invalid_Request", errorUrl, httpStatus);
        }
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private HttpClient getHttpClient() {
        return httpClient == null ? HttpClientBuilder.create().build() : httpClient;
    }

}
class SidecarClientAuthRequest {

    private final SidecarClient client;

    private final List<NameValuePair> formParameters;

    /**
     * Create a new AuthorizationRequest
     *
     * @param client   Must not be null
     * @param provider Must not be null
     */
    SidecarClientAuthRequest(SidecarClient client, String provider) {
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

class SidecarClientAuthResponse {

    private final SidecarClient client;

    private String queryString;

    /**
     * Create a new AuthorizationRequest
     *
     * @param client   Must not be null
     * @param queryString Must not be null
     */
    SidecarClientAuthResponse(SidecarClient client, String queryString) {
        this.client = client;
        this.queryString = queryString;

    }

    public SidecarClient build() {
        return client;
    }

    public SidecarClientAuthResponse setHttpClient(HttpClient httpClient) {
        this.client.setHttpClient(httpClient);
        return this;
    }
}