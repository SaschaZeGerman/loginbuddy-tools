package net.loginbuddy.tools.client;

import net.loginbuddy.tools.client.message.SidecarClientAuthRequest;
import net.loginbuddy.tools.client.message.SidecarClientAuthResponse;
import net.loginbuddy.tools.client.message.SidecarClientRefreshTokenRequest;
import net.loginbuddy.tools.common.connection.SidecarHttpClient;
import net.loginbuddy.tools.common.exception.LoginbuddyToolsException;
import net.loginbuddy.tools.common.model.LoginbuddyResponse;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import static org.junit.Assert.*;

public class SidecarClientTest {

    private SidecarClientAuthRequest scar;

    @Test
    public void testNoConnection() {
        scar = SidecarClient.createAuthRequest("myProvider");
        try {
            scar.build().getAuthorizationUrl();
            fail("No connection to loginbuddy-sidecar, should fail");
        } catch (LoginbuddyToolsException e) {
            System.out.println("SASCHA" + e.getMessage());
            assertEquals("connection_failed", e.getError());
            assertTrue(e.getErrorDescription().contains("Connection refused"));
            assertEquals(400, e.getHttpStatus());
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

    @Test
    public void testSimulateRefreshToken() {
        try {
            SidecarClientRefreshTokenRequest resp = SidecarClient.createRefreshTokenRequest("meRefreshToken").setHttpClient(new SidecarHttpClient(HttpClientBuilder.create().build()));
            LoginbuddyResponse lr = resp.setScope("a b c").build().getRefreshTokenResponse();
            assertEquals(200, lr.getStatus());
            assertEquals("FAKE_31f01303-f931-4218-a98f-eb673b522bee", lr.getOAuthDetails().getAccessToken());
            assertEquals(3600, lr.getOAuthDetails().getExpiresIn());
        } catch (LoginbuddyToolsException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testSimulateInvalidRefreshToken() {
        try {
            SidecarClientRefreshTokenRequest resp = SidecarClient.createRefreshTokenRequest("invalidRefreshToken").setHttpClient(new SidecarHttpClient(HttpClientBuilder.create().build()));
            LoginbuddyResponse lr = resp.build().getRefreshTokenResponse();
            assertEquals(400, lr.getStatus());
            assertEquals("invalid_request", lr.getError().getError());
            assertEquals("the given refresh_token is invalid", lr.getError().getErrorDescription());
        } catch (LoginbuddyToolsException e) {
            fail(e.getMessage());
        }
    }
}
