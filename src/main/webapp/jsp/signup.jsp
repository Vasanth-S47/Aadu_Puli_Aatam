<%@ page import="javax.servlet.ServletContext" %>
<%
    String BASE_URL = application.getInitParameter("BASE_URL");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign Up - Aadupuli Aatam</title>
    <link rel="stylesheet" href="<%= BASE_URL %>src/css/stylesLogin.css">
</head>
<body>
    <div class="header">
        <h1>Aadupuli Aatam</h1>
    </div>
    <div class="container">
        <h2>Sign Up</h2>
        <form id="signupForm" action="SignupServlet" method="post">
            <label for="name">Name:</label>
            <input type="text" id="name" name="name" placeholder="Enter Your Name" required>
            <small id="nameError" class="error"></small>

            <label for="signupEmail">Email:</label>
            <input type="email" id="signupEmail" name="email" placeholder="Enter Your Email" required>
            <small id="emailError" class="error"></small>

            <label for="signupPassword">Password:</label>
            <input type="password" id="signupPassword" name="password" placeholder="Enter Your Password" required>
            <small id="passwordError" class="error"></small>

            <label for="confirmPassword">Confirm Password:</label>
            <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Confirm Your Password" required>
            <small id="confirmPasswordError" class="error"></small>

             <p id="messageBox" class="hidden"></p>
            <button type="submit">Sign Up</button>
        </form>
        <p>Already have an account? <a href="login.jsp">Login here</a></p>
    </div>

    <script type="module" defer src="<%= BASE_URL %>src/js/signup.js"></script>
</body>
</html>
