# Loginbuddy-Tools

These tools are meant to simplify the usage of [Loginbuddy](https://github.com/SaschaZeGerman/loginbuddy). Although Loginbuddy itself already hides the complexity 
of OAuth and OpenID Connect, it can still be a challenge to connect to Loginbuddy via http or https.

For that reason, these tools include client-side code that hide the http layer by 99%. To explain what that means, below examples are provided.

**NOTE**: All examples are meant to work with *loginbuddy-sidecar*. In that case the web server that implements the client is running in the same 
docker network as *loginbuddy-sidecar*.

## Initiating the authorization code flow

Assuming there is a web UI that shows a button such as `Login with providerXYZ` and a click on that button calls a HttpServlets POST method.

The code below is placed in the POST method:

- Creating the authorization URL:
  - `...` // request validations are handled here
  - `String choosenProvider = httpRequest.getParameter("provider");`  // the choosen provider is also configured in Loginbuddys configuration
  - `String authorizationUrl = SidecarClient.createAuthRequest(choosenProvider).build().getAuthorizationUrl();`  // other parameters can be set before calling *build()*
  - `...` 
- Redirecting the user to the provider:
  - `...`
  - `httpResponse.sendRedirect(authorizationUrl);`
  - `...`

That's it, the user gets redirected to provider 'providerXYZ'.

## Processing the providers response

The provider responds to the redirect_uri and includes the authorization_code and the state parameter. Or, in case of a failure, error and error_description.

In either case Loginbuddy handles the response.

Assuming the callback is *https://myclient.com/callback*, this is the required implementation in the matching http servlets GET method:

- `...` // request validations are handled here
- `String queryString = request.getQueryString();`
- `LoginbuddyResponse loginbuddyResponse = SidecarClient.createAuthResponse(queryString).build().getAuthResponse();`  // Loginbuddy processes the query string, exchanges a code for an access_token

That's it, the object *loginbuddyResponse* contains everything your client needs! This means, it includes the issued access_token, the expanded content of the validated id_token, 
the userinfo response.

LoginbuddyResponse provides these methods:

- `loginbuddyResponse.getError();`  // if this is not null, an error occured. This should be called first
- `loginbuddyResponse.getOAuthDetails();`  // a json object containing: access_token, refresh_token, id_token, scope, token_type, expires_in, all default OAuth/OpenID Connect response values
- `loginbuddyResponse.getProviderDetails();`  // details about the chosen provider, including the /userinfo response and the content of the validated id_token
- `loginbuddyResponse.getLoginbuddyDetails();`  // details about Loginbuddy itself
- `loginbuddyResponse.getNormalizedDetails();`  // the most important one, a json object that has the same structure for all providers!
- `loginbuddyResponse.getState()();` // the state that was given to Loginbuddy by your client
- `loginbuddyResponse.getStatus()();` // the http status response Loginbuddy produced
- `loginbuddyResponse.toString()();` // overrides toString to return the complete response as string

## The client-sdk

### Build

To build the client-sdk these technologies and tools are needed:

- java jdk11
- maven
- make // this is for your convenience. If not available, its commands can be run manually

Building it is easy:

- `make build_all`

The client-sdk is placed here:

- `net.loginbuddy.tools.client-sdk/target/client-sdk-1.0.0.jar`

and installed into your local maven repository.

### Usage

Include the produced jar-file as a dependency in yor application:

```xml
        <dependency>
            <groupId>net.loginbuddy</groupId>
            <artifactId>client-sdk</artifactId>
            <version>1.0.0</version>
        </dependency>
```

The configuration is done via an environment variable:

- **LOGINBUDDY_SIDECAR_LOCATION**
  - if it is not set it defaults to *http://loginbuddy-sidecar:8444*
  - in docker-compose the host name *loginbuddy-sidecar* should be configured as service-name, container_name, hostname
  - you may also use *https://loginbuddy-sidecar:444* in which case your application has to accept loginbuddy-sidecar's self-signed SSL/TLS certificate

## Loginbuddy Samples

Checkout the [samples](https://github.com/SaschaZeGerman/loginbuddy-samples) project. The democlient implements the usage of the SDK found in *net.loginbuddy.democlient.sidecar*

For more details please see the [WIKI](https://github.com/SaschaZeGerman/loginbuddy-tools/wiki) of this project!