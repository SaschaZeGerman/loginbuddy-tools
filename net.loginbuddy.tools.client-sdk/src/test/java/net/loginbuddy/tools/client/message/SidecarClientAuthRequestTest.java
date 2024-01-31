package net.loginbuddy.tools.client.message;

import net.loginbuddy.tools.client.SidecarClient;
import net.loginbuddy.tools.common.oidc.Prompt;
import org.junit.Test;

import static org.junit.Assert.*;

public class SidecarClientAuthRequestTest {

    private SidecarClientAuthRequest scar;

    @Test
    public void testAuthRequest() {

        scar = SidecarClient.createAuthRequest("providerName");

        scar.setState("myState");
        scar.setScope("myScope");
        scar.setNonce("myNonce");

        assertEquals("providerName", scar.getParameters().stream().filter(param -> "provider".equals(param.getName())).findFirst().orElse(null).getValue());
        scar.setDynamicProvider("https://myIssuer:1234/value");
        assertEquals("", scar.getParameters().stream().filter(param -> "provider".equals(param.getName())).findFirst().orElse(null).getValue());

        scar.setPrompt(Prompt.LOGIN_CONSENT);
        scar.setLoginHint("myLogin@Hint.com");
        scar.setIdTokenHint("myIdTokenHint");
        scar.setSignedResponseAlgES256();  // RS256 is also available but the last one set wins
        scar.setObfuscateToken();
        scar.setTargetPath("/target/authorize");

        assertEquals("myScope", scar.getParameters().stream().filter(param -> "scope".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("myNonce", scar.getParameters().stream().filter(param -> "nonce".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("https://myIssuer:1234/value", scar.getParameters().stream().filter(param -> "issuer".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("login consent", scar.getParameters().stream().filter(param -> "prompt".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("myLogin@Hint.com", scar.getParameters().stream().filter(param -> "login_hint".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("myIdTokenHint", scar.getParameters().stream().filter(param -> "id_token_hint".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("ES256", scar.getParameters().stream().filter(param -> "signed_response_alg".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("true", scar.getParameters().stream().filter(param -> "obfuscate_token".equals(param.getName())).findFirst().orElse(null).getValue());
        assertEquals("/target/authorize", scar.getParameters().stream().filter(param -> "target_path".equals(param.getName())).findFirst().orElse(null).getValue());
    }

    @Test
    public void testProviderNull() {
        scar = SidecarClient.createAuthRequest(null);
        assertNull(scar.getParameters().stream().filter(param -> "provider".equals(param.getName())).findFirst().orElse(null).getValue());
    }
//
//    @Test
//    public void testAmICrazy() {
//        NameValuePair nvp01 = new SidecarBasicNameValuePair("key", "value1");
//        NameValuePair nvp02 = new SidecarBasicNameValuePair("key", "value2");
//        Set<NameValuePair> set = new HashSet<>();
//        set.add(nvp01);
//        set.add(nvp02);
//        assertEquals(1, set.size());
//        assertFalse(set.contains(nvp01));
//        assertTrue(set.contains(nvp02));
//    }

}