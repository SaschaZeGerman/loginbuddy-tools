package net.loginbuddy.tools.client;

import net.loginbuddy.tools.common.model.LoginbuddyResponse;
import net.loginbuddy.tools.common.exception.LoginbuddyToolsException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.logging.Logger;

public class SidecarClient {

    private static final Logger LOGGER = Logger.getLogger(String.valueOf(SidecarClient.class));

    private SidecarClientAuthRequest authRequest;
    private String loginbuddyInitUrl;
    private String loginbuddyCallbackUrl;

    private SidecarClient() {
        String loginbuddylocation = System.getenv("LOGINBUDDY_SIDECAR_LOCATION");
        if(loginbuddylocation != null) {
            loginbuddyInitUrl = loginbuddylocation.concat("/sidecar/initialize");
            loginbuddyCallbackUrl = loginbuddylocation.concat("/sidecar/callback?%s");
            LOGGER.info(String.format("ClientSDK connects to this location: %s", loginbuddylocation));
        } else {
            LOGGER.severe("ClientSDK cannot be initialized, missing environment variable LOGINBUDDY_SIDECAR_LOCATION");
        }
    }

    /**
     * This method initiates the process of creating the authorization URL that may be used to redirect a user to its authorization endpoint.
     *
     * @param provider The provider for which to create the authorization Url. This value must match a 'provider' in Loginbuddys configuration!
     * @return
     */
    public static SidecarClientAuthRequest createAuthRequest(String provider) {
        SidecarClient client = new SidecarClient();
        client.authRequest = new SidecarClientAuthRequest(client, provider);
        return client.authRequest;
    }

    /**
     * This will return the providers oauth response and details that were added by Loginbuddy.
     *
     * @param queryString The URL query component as received from the provider. Usually this will be '?code=aCode&state=aState' or '?error=anError&error_description=anErrorDescription'.
     *                    In either case it should be given to this method.
     * @return
     * @throws Exception
     */
    public static LoginbuddyResponse getAuthResponse(String queryString) throws Exception {
        SidecarClient client = new SidecarClient();
        HttpGet authResultRequest = new HttpGet(String.format(client.loginbuddyCallbackUrl, queryString));
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse authResultResponse = httpClient.execute(authResultRequest);
        String authResponseString = EntityUtils.toString(authResultResponse.getEntity());
        return new LoginbuddyResponse((JSONObject) new JSONParser().parse(authResponseString));
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
            HttpClient httpClient = HttpClientBuilder.create().build();
            initAuthResponse = httpClient.execute(initAuthRequest);

        } catch (Exception e) {
            LOGGER.warning(String.format("Loginbuddy could not be reached: %s", e.getMessage()));
            throw new LoginbuddyToolsException(e.getMessage());
        }

        if (initAuthResponse.getStatusLine().getStatusCode() == 201) {
            // the location header contains the authorizationUrl
            // for taking a browser to the target provider the URL should be used as is
            return initAuthResponse.getHeaders("Location")[0].getValue();
        } else {
            // Error!
            // status=400 // or other than 201
            // Location=http://localhost/?error={error}&error_description={errorDescription}
            String locationError = initAuthResponse.getHeaders("Location")[0].getValue();
            LOGGER.warning(locationError);
            throw new LoginbuddyToolsException(locationError);
        }
    }
}