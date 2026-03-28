<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Регистрация — MiniSite</title>
    <link rel="stylesheet" href="css/site.css">
</head>
<body class="auth-shell">
<%@ include file="includes/nav-public.jspf" %>
<main class="auth-main">
    <div class="auth-card">
        <h1>Регистрация</h1>
        <p class="sub">Создайте учётную запись для доступа к разделам сайта.</p>

        <%
            String error = request.getParameter("error");
            if (error != null) {
                if (error.equals("empty_fields")) {
                    out.println("<div class='alert alert--error'>Заполните обязательные поля.</div>");
                } else if (error.equals("user_exists")) {
                    out.println("<div class='alert alert--error'>Пользователь с таким логином или email уже существует.</div>");
                } else if (error.equals("db_error")) {
                    out.println("<div class='alert alert--error'>Ошибка базы данных. Попробуйте позже.</div>");
                }
            }
        %>

        <form action="RegisterServlet" method="post">
            <div class="form-field">
                <label for="username">Логин</label>
                <input type="text" id="username" name="username" required autocomplete="username">
            </div>
            <div class="form-field">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required autocomplete="email">
            </div>
            <div class="form-field">
                <label for="password">Пароль</label>
                <input type="password" id="password" name="password" required autocomplete="new-password">
            </div>
            <div class="form-field">
                <label for="fullName">Полное имя</label>
                <input type="text" id="fullName" name="fullName" placeholder="Необязательно" autocomplete="name">
            </div>
            <button type="submit" class="btn btn--primary">Зарегистрироваться</button>
        </form>

        <p class="auth-footer">Уже есть аккаунт? <a href="login.jsp">Войти</a></p>
    </div>
</main>
</body>
</html>
