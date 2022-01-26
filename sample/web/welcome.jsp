<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Loginbuddy-Tools</title>
</head>
<body>
<%
    if(session.getAttribute("email") != null) {
        response.getWriter().printf("<h2>Welcome %s!</h2>\n", session.getAttribute("email"));
    } else {
        response.sendRedirect("/sample/index.jsp");
    }
%>
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