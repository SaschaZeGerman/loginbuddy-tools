package net.loginbuddy.tools.common.model;

import org.json.simple.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LoginbuddyResponseTest {

    @Test
    public void testCompleteResponse() {

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

        LoginbuddyResponse lr = new LoginbuddyResponse(completeResponse);

        OAuthDetails od = lr.getOAuthDetails();
        assertEquals("an_access_token", od.getAccessToken());
        assertEquals("a_refresh_token", od.getRefreshToken());
        assertEquals("a_scope", od.getScope());
        assertEquals("an_id_token", od.getIdToken());
        assertEquals(3600, od.getExpiresIn());

        LoginbuddyDetails ld = lr.getLoginbuddyDetails();
        assertEquals("audience", ld.getAud());
        assertEquals("issuer", ld.getIss());
        assertEquals("noncevalue", ld.getNonce());
        assertEquals( 1234567890L, ld.getIat());

        JSONObject normalized = lr.getNormalizedDetails();
        assertEquals("mysub", normalized.get("sub"));
        assertEquals("myprovider", normalized.get("provider"));
    }
}
