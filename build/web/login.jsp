<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Вход — MiniSite</title>
    <link rel="stylesheet" href="css/site.css">
</head>
<body class="auth-shell">
<%@ include file="includes/nav-public.jspf" %>
<main class="auth-main">
    <div class="auth-card">
        <h1>Вход</h1>
        <p class="sub">Введите логин или email и пароль.</p>

        <%
            String error = request.getParameter("error");
            String success = request.getParameter("success");

            if (error != null) {
                if (error.equals("empty_fields")) {
                    out.println("<div class='alert alert--error'>Введите логин и пароль.</div>");
                } else if (error.equals("invalid")) {
                    out.println("<div class='alert alert--error'>Неверный логин или пароль.</div>");
                }
            }
            if (success != null && success.equals("registered")) {
                out.println("<div class='alert alert--success'>Регистрация успешна. Войдите в систему.</div>");
            }
        %>

        <form action="LoginServlet" method="post">
            <div class="form-field">
                <label for="username">Логин или email</label>
                <input type="text" id="username" name="username" placeholder="Логин или email" required autocomplete="username">
            </div>
            <div class="form-field">
                <label for="password">Пароль</label>
                <input type="password" id="password" name="password" placeholder="Пароль" required autocomplete="current-password">
            </div>
            <button type="submit" class="btn btn--primary">Войти</button>
        </form>

        <p class="auth-footer">Нет аккаунта? <a href="register.jsp">Регистрация</a></p>
    </div>
</main>
</body>
</html>
