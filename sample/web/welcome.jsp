<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Loginbuddy-Tools</title>
    <script>
        function setWelcomeMessage() {
            let welcome = document.getElementById('idWelcome');
            let index = location.href.indexOf('#');
            if (index >= 0) {
                // ...#email={email-address}...
                let email = location.href.substring(index + 7);
                welcome.innerText = 'Welcome ' + decodeURIComponent(email) + '!';
            } else {
                welcome.innerText = 'Welcome ... authentication was successful, but something is wrong ... please check the logs!';
            }
        }
    </script>
</head>
<body onload="setWelcomeMessage();">
<h2 id="idWelcome"></h2>
<p>If you see this content you have successfully logged in using Loginbuddy-Sidecar with Loginbuddy-Tools.</p>
<p>To learn more about the implemented, checkout the source code at <strong>./sample</strong> in the project.</p>
<p>To logout, hit the button below</p>
<hr/>
<form action="index.jsp" method="POST">
    <input type="submit" value="logout" name="action" size="20">
</form>
<hr/>
</body>
</html>