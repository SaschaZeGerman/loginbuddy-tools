package net.loginbuddy.tools.client;

import net.loginbuddy.tools.client.message.ParameterProvider;
import net.loginbuddy.tools.client.message.SidecarClientAuthRequest;
import net.loginbuddy.tools.client.message.SidecarClientAuthResponse;
import net.loginbuddy.tools.client.message.SidecarClientRefreshTokenRequest;
import net.loginbuddy.tools.common.exception.LoginbuddyToolsException;
import net.loginbuddy.tools.common.model.LoginbuddyResponse;
import net.loginbuddy.tools.common.model.OAuthDetails;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SidecarClient {

    private static final Logger LOGGER = Logger.getLogger(String.valueOf(SidecarClient.class));

    // http://localhost/?error={error}&error_description={errorDescription}
    private static final Pattern pErrorUrl = Pattern.compile("[?]error=([a-zA-Z0-9-_ ]{0,48})[&]error_description=([a-zA-Z0-9-_ ]{0,128})");

    private SidecarClientAuthRequest authRequest;
    private SidecarClientAuthResponse authResponse;
    private SidecarClientRefreshTokenRequest refreshTokenRequest;
    private String loginbuddyInitUrl;
    private String loginbuddyCallbackUrl;
    private String loginbuddyTokenEndpoint;

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
            loginbuddyTokenEndpoint = loginbuddylocation.concat("/sidecar/token");
            LOGGER.info(String.format("ClientSDK connects to this location: %s", loginbuddylocation));
        } catch(RuntimeException e) {
            LOGGER.severe(String.format("Environment variable LOGINBUDDY_SIDECAR_LOCATION is not accessible, clientSDK is not usable: %s", e.getMessage()));
        }
    }

    /**
     * This method creates an authorization request which makes the authorizationUrl for the given provider available.
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
     * This method creates a refresh_token request.
     *
     * @param refreshToken The refresh_token to be exchange for a new token response
     * @return
     */
    public static SidecarClientRefreshTokenRequest createRefreshTokenRequest(String refreshToken) {
        SidecarClient client = new SidecarClient();
        client.refreshTokenRequest = new SidecarClientRefreshTokenRequest(client, refreshToken);
        return client.refreshTokenRequest;
    }

    /**
     * This method retrieves the authorization response as created by the provider and details added by Loginbuddy.
     *
     * @return Loginbuddys response including all provider and Loginbuddy details
     * @throws Exception if the connection to Loginbuddy failed or Loginbuddy returned a 400 response
     */
    public LoginbuddyResponse getAuthResponse() throws LoginbuddyToolsException {
        HttpGet authResultRequest = new HttpGet(String.format(loginbuddyCallbackUrl, this.authResponse.getQueryString()));
        try {
            return getLoginbuddyResponse(getHttpClient().execute(authResultRequest));
        } catch (IOException e) {
            throw new LoginbuddyToolsException("invalid_request", e.getMessage());
        }
    }

    /**
     * After creating a SidecarClientAuthRequest this method returns the authorization URL that can be used to initiate the authorization request with the provider.
     *
     * @return The authorization URL
     * @throws LoginbuddyToolsException If no request had been created or if Loginbuddy could not b reached
     */
    public String getAuthorizationUrl() throws LoginbuddyToolsException {

        // initialize the authorization flow. Loginbuddy will return the authorizationUrl that is valid for the selected provider
        HttpResponse initAuthResponse = doPost(loginbuddyInitUrl, authRequest);

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

    /**
     * USe an existing refresh_token to get a new token response
     *
     * @return The authorization URL
     * @throws LoginbuddyResponse
     */
    public LoginbuddyResponse getRefreshTokenResponse() throws LoginbuddyToolsException {
        HttpResponse initAuthResponse = doPost(loginbuddyTokenEndpoint, refreshTokenRequest);
        return getLoginbuddyResponse(initAuthResponse);
    }

    private HttpResponse doPost(String targetUrl, ParameterProvider parameterProvider) throws LoginbuddyToolsException {
        try {
            HttpPost postRequest = new HttpPost(targetUrl);
            postRequest.setEntity(new UrlEncodedFormEntity(parameterProvider.getParameters()));
            return getHttpClient().execute(postRequest);
        } catch (Exception e) {
            LOGGER.warning(String.format("Loginbuddy could not be reached: %s", e.getMessage()));
            throw new LoginbuddyToolsException("connection_failed", e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    private LoginbuddyResponse getLoginbuddyResponse(HttpResponse httpResponse) throws LoginbuddyToolsException {
        try {
            String authResponseString = EntityUtils.toString(httpResponse.getEntity());
            return new LoginbuddyResponse(httpResponse.getFirstHeader("X-State").getValue(), httpResponse.getStatusLine().getStatusCode(), (JSONObject) new JSONParser().parse(authResponseString));
        } catch(Exception e) {
            throw new LoginbuddyToolsException("auth_response_error", e.getMessage(), httpResponse == null ? 400 : httpResponse.getStatusLine().getStatusCode());
        }
    }

    /**
     * Configure a http client. This, however, is intended for testing purposes and should be ignored otherwise
     * @param httpClient
     */
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private HttpClient getHttpClient() {
        return httpClient == null ? HttpClientBuilder.create().build() : httpClient;
    }

}

