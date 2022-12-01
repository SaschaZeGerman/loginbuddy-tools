package net.loginbuddy.tools.sample.login;

import net.loginbuddy.tools.client.SidecarClient;
import net.loginbuddy.tools.common.model.LoginbuddyResponse;
import net.loginbuddy.tools.common.exception.LoginbuddyToolsException;
import net.loginbuddy.tools.common.oidc.Prompt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Sidecar extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            Here first request validations should be implemented such as parameter length and content.
            In this demo we assume there is just one 'friendly' parameter called 'provider'
         */
        try {
            /*
                - Select incoming parameters like chosen provider
                - Chosen provider MUST be configured in Loginbuddys config.json file
                - Request the authorizationUrl from Loginbuddy
                - redirect the user to that location
             */
            String provider = req.getParameter("provider");
            String authUrl = "";
            if("server_loginbuddy_dynamic".equalsIgnoreCase(provider)) {
                authUrl = SidecarClient.createAuthRequest(provider)
                        .setObfuscateToken()
                        .setPrompt(Prompt.LOGIN_CONSENT)
                        .setDynamicProvider("https://server.loginbuddy.net")
                        .build().getAuthorizationUrl();
            } else {
                /*
                   accept only configured providers!
                   for this demo project any given provider is used to keep it flexible
                   Loginbuddy will fail anyways
                */
                authUrl = SidecarClient.createAuthRequest(provider).build().getAuthorizationUrl();
            }

            resp.sendRedirect(authUrl);
        } catch (LoginbuddyToolsException e) {
            e.printStackTrace();
            resp.sendError(400, String.format("Something went wrong: error: %s, error_description: %s", e.getError(), e.getErrorDescription()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            As for POST, first validation should be implemented here too.
            The url query component will either contain 'code' and 'state' or 'error' and 'error_description'.
            Our code does not need to worry about that, Loginbuddy will handle both cases.
         */
        String query = req.getQueryString();
        try {
            LoginbuddyResponse loginbuddyResponse = SidecarClient.getAuthResponse(query);
            /*
              This is where many more details may be retrieved from Loginbuddys response.
             */
            String email = (String)loginbuddyResponse.getNormalizedDetails().get("email");
            resp.sendRedirect(String.format("/welcome.jsp#email=%s", URLEncoder.encode(email, StandardCharsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(400, String.format("Something went wrong: %s", e.getMessage()));
        }
    }
}