package net.loginbuddy.tools.common.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ErrorResponseTest {

    private ErrorResponse resp;

    @Before
    public void setup() {
        resp = new ErrorResponse(400, "error_type", "error_description");
    }

    @Test
    public void testError() {
        assertEquals("error_type", resp.getError());
    }

    @Test
    public void testErrorDescription() {
        assertEquals("error_description", resp.getErrorDescription());
    }

    @Test
    public void testErrorStatus() {
        assertEquals(400, resp.getStatus());
    }

    @Test
    public void testErrorUrlEncoded() {
        resp = new ErrorResponse(401, "error type", "error description");
        assertEquals("error=error%20type&error_description=error%20description", resp.getUrlEncodedError());
    }
}
