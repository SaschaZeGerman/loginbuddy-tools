# Loginbuddy-Tools

These tools are meant to simplify the usage of [Loginbuddy](https://github.com/SaschaZeGerman/loginbuddy). Although Loginbuddy itself already hides the complexity 
of OAuth and OpenID Connect, it can still be a challenge to connect to Loginbuddy via http or https.

For that reason, these tools include client-side code that hide the http layer by 99%. To explain what that means, below examples are provided.

**NOTE**: All examples are meant to work with *loginbuddy-sidecar*. In that case the web server (servlet container) that implements the client is running in the same 
docker network as *loginbuddy-sidecar*.

## Initiating the authorization code flow

Assuming there is a web UI that shows a button such as `Login with providerXYZ` and a click on that button calls a HttpServlets GET method.

The code below is placed in the GET method:

- Creating the authorization URL:
  - `...` // request validations are handled here
  - `String choosenProvider = httpRequest.getParameter("provider");`  // the choosenProvider is also configured in Loginbuddys configuration
  - `String authorizationUrl = SidecarClient.createAuthRequest(choosenProvider).build().getAuthorizationUrl();`  // other parameters can be set before calling *build()*
  - `...` 
- Redirecting the user to the provider:
  - `...`
  - `httpResponse.sendRedirect(authorizationUrl);`
  - `...`

That's it, the user gets redirected to provider 'providerXYZ'.

## Processing the providers response

The provider responds to the redirect_uri and includes the authorization_code and the state parameter. Or, in case of an error, error and error_description.

In either case Loginbuddy handles the response.

Assuming the callback is *https://local.loginbuddy.net/callback*, this is the required implementation in the matching http servlets GET method:

- `...` // request validations are handled here
- `LoginbuddyResponse authResponse = SidecarClient.getAuthResponse(request.getQueryString());`  // Loginbuddy procsses the query string, exchanges a code for an access_token

That's it, the object *authResponse* contains everything your client needs! This means, it includes the issued access_token, the expanded content of the validated id_token, 
the userinfo response.

LoginbuddyResponse provides these methods:

- `authResponse.getError();`  // if this is not null, an error occured. This should be called first
- `authResponse.getOAuthResponse();`  // a json object containing: access_token, refresh_token, id_token, scope, token_type, expires_in, all default OAuth/OpenID Connect response values
- `authResponse.getDetailsProvider();`  // details about the choosen provider, including the /userinfo response and the content of the validated id_token
- `authResponse.getDetailsLoginbuddy();`  // details about Loginbuddy itself
- `authResponse.getDetailsNormalized();`  // the most important one, a json object that has the same structure for all providers!

## Sample project

To get started a sample web project is provided.

### Building

- `cd ./apitest`
- `docker-compose up`
- `../`
- `make build_sample`
- `cd ./apitest`
- `ctrl + c`
- `docker-compose down`
- `cd ../sample`
- `docker-compose up`
- browser: `http://localhost`  // should end up with 'Welcome' message
- `ctrl + c`
- `docker-compose down`