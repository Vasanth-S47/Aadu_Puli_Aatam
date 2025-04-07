<%@ page import="javax.servlet.ServletContext" %>
<%
    String BASE_URL = application.getInitParameter("BASE_URL");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Aadupuli Aatam</title>
    <link rel="stylesheet" href="<%= BASE_URL %>src/css/stylesLogin.css">
</head>
<body>
    <div class="header">
        <h1>Aadupuli Aatam</h1>
    </div>
    <div class="container">
        <h2>Login</h2>
        <form id="loginForm" action="LoginServlet" method="post">
            <label for="loginEmail">Email:</label>
            <input type="email" id="loginEmail" name="email" placeholder="Enter Your Email">
            <small id="emailError" class="error"></small>
            <label for="loginPassword">Password:</label>
            <input type="password" id="loginPassword" name="password" placeholder="Enter Your Password">
            <small id="passwordError" class="error"></small>
            <p id="messageBox" class="hidden"></p>
            <button type="submit">Login</button>
        </form>
        <p>Don't have an account? <a href="signup.jsp">Sign up here</a></p>
    </div>
    <script type="module" defer src="<%= BASE_URL %>src/js/login.js"></script>
</body>
</html>
