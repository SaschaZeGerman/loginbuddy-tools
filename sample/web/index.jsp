<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Loginbuddy-Tools</title>
</head>
<body>
<%
    if(request.getSession().getAttribute("email") != null && request.getParameter("action").equals("logout")) {
        request.getSession().removeAttribute("email");
    }
%>
<h2>Sample project to demonstrate Loginbuddy-Tools</h2>
<p>This simple project is meant to get anyone started using Loginbuddy and Loginbuddy-Tools</p>
<p>After using and looking at the inside of this sample project it hopefully becomes apparent why these tools with Loginbuddy are useful
to anyone who is developing an OAuth/ OpenID Connect enabled application.</p>
<h3>Loginbuddy-Sidecar</h3>
<p>Loginbuddy supports different deployment models. This section shows off how <a href="https://github.com/SaschaZeGerman/loginbuddy/wiki/Deployment" target="_blank">Loginbuddy-Sidecar</a> can be used using Loginbuddy-Tools.</p>
<p>The form below let's anyone login with any credentials. The <strong>simulation</strong> consists of these parts:</p>
<ol>
    <li><strong>Loginbuddy Demoserver: </strong>This is a demo OpenID Connect provider that accepts any credentials</li>
    <li><strong>Loginbuddy Sidecar: </strong>This is the Loginbuddy container that communicates with the provider and handles all OAuth and OpenID Connect related tasks</li>
    <li><strong>Demo client: </strong>This server, which connects to Loginbuddy-Sidecar</li>
</ol>

<h4>Demo Login Form</h4>
<p>A click on the button <strong>Login with Demo provider</strong> is received here:</p>
<code>./sample/src/main/java/net/loginbuddy/tools/sample/login/Sidecar.java#POST</code>
<p>After clicking the button the browser gets redirected to the demo provider. Login, grant the request and you will find yourself back here.</p>
<hr/>
<form action="login" method="POST" enctype="application/x-www-form-urlencoded">
    <input type="submit" value="server_loginbuddy_dynamic" name="provider" size="20">
    <br/>
    <input type="text" value="" name="provider" size="20">
    <input type="submit" value="" name="provider" size="20">
</form>
<hr/>
<h3>Sequence Diagram</h3>
<p>Below is the notation for a sequence diagram which can be used at <a href="https://websequencediagrams.com" target="_blank">websequencediagrams.com</a>. It is free and only required copy/paste in the browser:</p>
<pre>
    title Loginbuddy Sample\nSascha Preibisch, June 2021

    participant User as user
    participant Browser as browser
    participant Demo client as client
    participant Loginbuddy Sidecar as sidecar
    participant Demo provider as provider

    user->browser: click 'login with Demo provider'
    browser->client: /login?provider='Demo provider'
    note right of client: 'Demo client' uses a java method to retireve the authUrl
    client->sidecar: getAuthorizationUrl('Demo provider')
    sidecar->sidecar: validate request\ncheck for provider configuration\ngenerate authorizationUrl
    sidecar->client: authorizationUrl
    note right of client: 'authorizationUrl' is returned by the method that was called\nThe communication to sidecar is tranparent to 'Demo client'
    client->browser: 302, Location={authorizationUrl}
    browser->provider: /authorizationUrl
    provider->provider: user logs in\ngrants access\nprovider issues authorization_code
    provider->browser: 302, redirect_uri?code=anAuthCode&state=aState
    browser->client: /redirect_uri?code=anAuthCode&state=aState
    client->client: client extract the\nquery component\n'code=anAuthCode&state=state
    note right of client: 'Demo client' is calling a java method again
    client->sidecar: getAuthResponse(code=anAuthCode&state=aState)
    sidecar->provider: /token\nexchange code for token
    provider->sidecar: issue token response
    sidecar->sidecar: validate response,\nprepare response for client
    sidecar->client: loginbuddyResponse\nincludes all details
    client->client: extract desired details such as 'first name' or 'email'
    client->browser: 'Welcome John Smith (john.smith@example.com)'
    </pre>
</body>
</html>