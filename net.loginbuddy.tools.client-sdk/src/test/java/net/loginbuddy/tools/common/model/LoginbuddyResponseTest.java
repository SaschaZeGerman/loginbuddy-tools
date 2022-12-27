package net.loginbuddy.tools.common.model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

import java.io.FileReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LoginbuddyResponseTest {

    @Test
    public void testCompleteResponseManual() {

        JSONObject providerDetailsIdTokenPayload = new JSONObject();
        providerDetailsIdTokenPayload.put("sub", "mysub");
        providerDetailsIdTokenPayload.put("email_verified", true);

        JSONObject providerDetailsUserinfoResponse = new JSONObject();
        providerDetailsIdTokenPayload.put("sub", "mysub");
        providerDetailsIdTokenPayload.put("given_name", "my given name");

        JSONObject providerDetails = new JSONObject();
        providerDetails.put("provider", "myprovider");
        providerDetails.put("iss", "issuer");
        providerDetails.put("nonce", "noncevalue");
        providerDetails.put("iat", 1234567890L);
        providerDetails.put("id_token_payload", providerDetailsIdTokenPayload);
        providerDetails.put("userinfo", providerDetailsUserinfoResponse);

        JSONObject loginbuddyDetails = new JSONObject();
        loginbuddyDetails.put("aud", "audience");
        loginbuddyDetails.put("iss", "issuer");
        loginbuddyDetails.put("nonce", "noncevalue");
        loginbuddyDetails.put("iat", 1234567890L);

        JSONObject normalizedDetails = new JSONObject();
        normalizedDetails.put("sub", "mysub");
        normalizedDetails.put("provider", "myprovider");

        JSONObject completeResponse = new JSONObject();
        completeResponse.put("access_token", "an_access_token");
        completeResponse.put("refresh_token", "a_refresh_token");
        completeResponse.put("scope", "a_scope");
        completeResponse.put("token_type", "a_token_type");
        completeResponse.put("id_token", "an_id_token");
        completeResponse.put("expires_in", 3600);
        completeResponse.put("details_provider", providerDetails);
        completeResponse.put("details_loginbuddy", loginbuddyDetails);
        completeResponse.put("details_normalized", normalizedDetails);

        LoginbuddyResponse lr = new LoginbuddyResponse("teststate", 200, completeResponse);

        OAuthDetails od = lr.getOAuthDetails();
        assertEquals("an_access_token", od.getAccessToken());
        assertEquals("a_refresh_token", od.getRefreshToken());
        assertEquals("a_scope", od.getScope());
        assertEquals("an_id_token", od.getIdToken());
        assertEquals("a_token_type", od.getTokenType());
        assertEquals(3600, od.getExpiresIn());

        LoginbuddyDetails ld = lr.getLoginbuddyDetails();
        assertEquals("audience", ld.getAud());
        assertEquals("issuer", ld.getIss());
        assertEquals("noncevalue", ld.getNonce());
        assertEquals( 1234567890L, ld.getIat());

        ProviderDetails pd = lr.getProviderDetails();
        assertEquals("myprovider", pd.getProvider());

        JSONObject normalized = lr.getNormalizedDetails();
        assertEquals("mysub", normalized.get("sub"));
        assertEquals("myprovider", normalized.get("provider"));

        assertEquals("teststate", lr.getState());
        assertEquals(200, lr.getStatus());
    }

    @Test
    public void testCompleteResponse() {
        try {
            LoginbuddyResponse lr = new LoginbuddyResponse("state", 200, (JSONObject)new JSONParser().parse(new FileReader("src/test/resources/testResponse.json")));
            assertEquals(200, lr.getStatus());
            assertEquals("state", lr.getState());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testLoginbuddyDetails() {
        try {
            LoginbuddyResponse lr = new LoginbuddyResponse("state", 200, (JSONObject)new JSONParser().parse(new FileReader("src/test/resources/testResponse.json")));
            assertEquals("clientIdForTestingPurposes", lr.getLoginbuddyDetails().getAud());
            assertEquals("https://latest.loginbuddy.net", lr.getLoginbuddyDetails().getIss());
            assertEquals("2c7964a1-62d2-4fb4-ad3d-dac90b556bec", lr.getLoginbuddyDetails().getNonce());
            assertEquals(1671424953, lr.getLoginbuddyDetails().getIat());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProviderDetails() {
        try {
            LoginbuddyResponse lr = new LoginbuddyResponse("state", 200, (JSONObject)new JSONParser().parse(new FileReader("src/test/resources/testResponse.json")));
            assertEquals("server_loginbuddy", lr.getProviderDetails().getProvider());
            assertEquals("Login Buddy", ((JSONObject)lr.getProviderDetails().getDetails().get("userinfo")).get("name"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testMinOAuthDetails() {
        try {
            LoginbuddyResponse lr = new LoginbuddyResponse("state", 200, (JSONObject)new JSONParser().parse(new FileReader("src/test/resources/testResponseMinOAuth.json")));
            assertEquals("FAKE_31f01303-f931-4218-a98f-eb673b522bee", lr.getOAuthDetails().getAccessToken());
            assertEquals("Bearer", lr.getOAuthDetails().getTokenType());
            assertEquals("openid profile email", lr.getOAuthDetails().getScope());
            assertEquals(3600, lr.getOAuthDetails().getExpiresIn());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
