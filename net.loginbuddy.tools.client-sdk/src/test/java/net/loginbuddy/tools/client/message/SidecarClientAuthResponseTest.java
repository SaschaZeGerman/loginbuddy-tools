package net.loginbuddy.tools.client.message;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SidecarClientAuthResponseTest {

    @Test
    public void testAuthResponse() {
        SidecarClientAuthResponse scar = new SidecarClientAuthResponse(null, "code=aCode&state=aState");
        assertEquals("code=aCode&state=aState", scar.getQueryString());
    }
}
