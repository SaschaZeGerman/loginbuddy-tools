package net.loginbuddy.tools.client;

import net.loginbuddy.tools.common.model.LoginbuddyResponse;
import net.loginbuddy.tools.common.exception.LoginbuddyToolsException;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class SidecarClientTest {

        private String state;

        @Before
        public void setup() {
            state = "25a8a0b3-5bf5-4217-98cf-6ad8e430608f";
        }

        @Test
        public void testCreateSidecarClient() {
            try {
                // default usage, only provider is configured. Replacing other values only to normalize them for this test
                String actual = SidecarClient.createAuthRequest("server_loginbuddy").build().getAuthorizationUrl().replaceAll("nonce=[a-z0-9-]{36}", "nonce=27054b6d-0536-42a6-b70f-0e2f01e048b6").replaceAll("code_challenge=[a-zA-Z0-9-_]{42,128}", "code_challenge=znQceKuTsJtPJZp0bG1ggYqX4KVu-XQsuGpqvyy-QhQ").replaceAll("state=[a-z0-9-]{36}", String.format("state=%s", state));
                String expected = "https://demoserver.loginbuddy.net/authorize?client_id=loginbuddy_demoId&response_type=code&scope=server_scope&nonce=27054b6d-0536-42a6-b70f-0e2f01e048b6&redirect_uri=https%3A%2F%2Flocal.loginbuddy.net%2Fcallback&code_challenge=znQceKuTsJtPJZp0bG1ggYqX4KVu-XQsuGpqvyy-QhQ&code_challenge_method=S256&state="+state;
                assertEquals(expected,actual);
            } catch (LoginbuddyToolsException e) {
                fail(e.getMessage());
            }
        }

        @Test
        public void testCreateSidecarClientUnkonwnProvider() {
            // default usage, only provider is configured
            try {
                SidecarClient.createAuthRequest("unknown").build().getAuthorizationUrl();
                fail("should fail due to an unknown provider");
            } catch (LoginbuddyToolsException e) {
                assertTrue(true);
            }
        }

        @Test
        public void testGetProviderResponseQueryString() {
            LoginbuddyResponse authResponse = null;
            try {
                String authUrl = SidecarClient.createAuthRequest("server_loginbuddy").build().getAuthorizationUrl();
                Matcher actualState = Pattern.compile("state=([0-9a-z-]{36})").matcher(authUrl);
                actualState.find();
                authResponse = SidecarClient.getAuthResponse(String.format("code=acode&state=%s", actualState.group(1)));
                assertNotNull(authResponse.getError());
                assertEquals("invalid_request", authResponse.getError().getError());
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
}