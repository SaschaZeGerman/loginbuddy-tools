package net.loginbuddy.tools.common.oidc;

public enum Prompt {

    NONE("none"),
    LOGIN("login"),
    LOGIN_CONSENT("login consent"),
    LOGIN_SELECT_ACCOUNT("login select_account"),
    LOGIN_CONSENT_SELECT_ACCOUNT("login consent select_account"),
    CONSENT("consent"),
    CONSENT_SELECT_ACCOUNT("consent select_account"),
    SELECT_ACCOUNT("select_account");

    private String prompt;

    Prompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public String toString() {
        return prompt;
    }
}
