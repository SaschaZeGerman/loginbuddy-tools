package net.loginbuddy.tools.client;

import net.loginbuddy.tools.client.message.SidecarClientAuthRequest;
import net.loginbuddy.tools.client.message.SidecarClientAuthResponse;
import net.loginbuddy.tools.common.connection.SidecarHttpClient;
import net.loginbuddy.tools.common.exception.LoginbuddyToolsException;
import net.loginbuddy.tools.common.model.LoginbuddyResponse;
import net.loginbuddy.tools.common.oidc.Prompt;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import static org.junit.Assert.*;

public class SidecarClientAuthRequestTest {

    private SidecarClientAuthRequest scar;

    @Test
    public void testAllParameters() {

        scar = SidecarClient.createAuthRequest("providerName");

        scar.setState("myState");
        scar.setScope("myScope");
        scar.setNonce("myNonce");
        scar.setDynamicProvider("https://myIssuer:1234/value");
        scar.setPrompt(Prompt.LOGIN_CONSENT);
        scar.setLoginHint("myLogin@Hint.com");
        scar.setIdTokenHint("myIdTokenHint");
        scar.setSignedResponseAlgES256();  // RS256 is also available but the last one set wins
        scar.setObfuscateToken();

        assertEquals("providerName", scar.getParameters().stream().filter(param -> "provider".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("myScope", scar.getParameters().stream().filter(param -> "scope".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("myNonce", scar.getParameters().stream().filter(param -> "nonce".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("https://myIssuer:1234/value", scar.getParameters().stream().filter(param -> "issuer".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("login consent", scar.getParameters().stream().filter(param -> "prompt".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("myLogin@Hint.com", scar.getParameters().stream().filter(param -> "login_hint".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("myIdTokenHint", scar.getParameters().stream().filter(param -> "id_token_hint".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("ES256", scar.getParameters().stream().filter(param -> "signed_response_alg".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("true", scar.getParameters().stream().filter(param -> "obfuscate_token".equals(param.getName())).findFirst().orElse(null).getValue());
    }

    @Test
    public void testProviderNull() {
        scar = SidecarClient.createAuthRequest(null);
        assertNull(scar.getParameters().stream().filter(param -> "provider".equals(param.getName())).findFirst().orElse(null).getValue());
    }

    @Test
    public void testNoConnection() {
        scar = SidecarClient.createAuthRequest("myProvider");
        try {
            scar.build().getAuthorizationUrl();
            fail("No connection to loginbuddy-sidecar, should fail");
        } catch (LoginbuddyToolsException e) {
            assertEquals("connection_failed", e.getError());
            assertEquals("Connection refused", e.getErrorDescription());
            assertEquals(-1, e.getHttpStatus());
        }
    }

    @Test
    public void testSimulateGetAuthUrl() {
        scar = SidecarClient.createAuthRequest("myProvider").setHttpClient(new SidecarHttpClient(HttpClientBuilder.create().build()));
        try {
            assertEquals("http://localhost", scar.build().getAuthorizationUrl());
        } catch (LoginbuddyToolsException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testSimulateGetAuthResponse() {
        try {
            SidecarClientAuthResponse resp = SidecarClient.createAuthResponse("code=aCode&state=aState").setHttpClient(new SidecarHttpClient(HttpClientBuilder.create().build()));
            LoginbuddyResponse lr = resp.build().getAuthResponse();
            assertEquals(200, lr.getStatus());
            assertEquals("FAKE_31f01303-f931-4218-a98f-eb673b522bee", lr.getOAuthDetails().getAccessToken());
            assertEquals(3600, lr.getOAuthDetails().getExpiresIn());
        } catch (LoginbuddyToolsException e) {
            fail(e.getMessage());
        }
    }
}