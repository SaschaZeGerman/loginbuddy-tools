package net.loginbuddy.tools.common.oidc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PromptTest {

    @Test
    public void testAll() {
        assertEquals("consent", Prompt.CONSENT.toString());
        assertEquals("consent select_account", Prompt.CONSENT_SELECT_ACCOUNT.toString());
        assertEquals("login", Prompt.LOGIN.toString());
        assertEquals("login consent", Prompt.LOGIN_CONSENT.toString());
        assertEquals("login select_account", Prompt.LOGIN_SELECT_ACCOUNT.toString());
        assertEquals("login consent select_account", Prompt.LOGIN_CONSENT_SELECT_ACCOUNT.toString());
        assertEquals("select_account", Prompt.SELECT_ACCOUNT.toString());
        assertEquals("none", Prompt.NONE.toString());
    }

}
