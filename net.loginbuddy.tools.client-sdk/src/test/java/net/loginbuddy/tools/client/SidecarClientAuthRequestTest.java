package net.loginbuddy.tools.client;

import net.loginbuddy.tools.common.exception.LoginbuddyToolsException;
import net.loginbuddy.tools.common.oidc.Prompt;
import org.junit.Before;
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
            fail("No connection to loginbudyd-sidecar, should fail");
        } catch (LoginbuddyToolsException e) {
            assertEquals("error_description", e.getErrorDescription());
            assertEquals("error", e.getError());
            assertTrue(e.getMessage(), true);
        }
    }
}
