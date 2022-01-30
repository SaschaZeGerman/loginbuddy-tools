package net.loginbuddy.tools.sample.login;

import net.loginbuddy.tools.client.SidecarClient;
import net.loginbuddy.tools.common.LoginbuddyResponse;
import net.loginbuddy.tools.common.exception.LoginbuddyToolsException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

public class Sidecar extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            Here first request validations should be implemented such as parameter length and content.
            In this demo we assume there is just one 'friendly' parameter called 'provider'
         */
        try {
            /*
                This step is sending a request to Loginbuddy.
                Loginbuddy will do all necessary validations before generating and returning the authorizationUrl.
             */
            String provider = req.getParameter("provider");
            String authUrl = SidecarClient.createAuthRequest(provider).build().getAuthorizationUrl();
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
            String email = (String)loginbuddyResponse.getDetailsNormalized().get("email");
            resp.sendRedirect(String.format("/welcome.jsp#email=%s", URLEncoder.encode(email, "UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(400, String.format("Something went wrong: %s", e.getMessage()));
        }
    }
}