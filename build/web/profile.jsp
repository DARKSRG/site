<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.User"%>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Мой профиль — MiniSite</title>
    <link rel="stylesheet" href="css/site.css">
</head>
<body class="app">
<%@ include file="includes/nav-auth.jspf" %>
<main class="main">
    <div class="container">
        <h1 class="page-title">Личный кабинет</h1>
        <p class="page-desc">Добро пожаловать в систему учёта носителей.</p>

        <div class="card">
            <h2 class="section-title">Ваш профиль</h2>
            <p><strong>Имя:</strong> <%= user.getFullName() != null && !user.getFullName().isEmpty() ? user.getFullName() : user.getUsername() %></p>
            <p><strong>Логин:</strong> <%= user.getUsername() %></p>
            <p><strong>Email:</strong> <%= user.getEmail() %></p>
            <p class="mb-0"><strong>Дата регистрации:</strong> <%= user.getCreatedAt() %></p>
        </div>

        <div class="card card--muted">
            <h2 class="section-title">Разделы</h2>
            <ul style="margin:0; padding-left:1.2rem; line-height:1.9; color: var(--text-muted);">
                <li><a href="client_cards.jsp">Учётные карточки клиентов</a></li>
                <li><a href="media_receipts.jsp">Поступление дисков и кассет</a></li>
                <li><a href="media_sales.jsp"><strong>Продажа</strong> — сведения о продаже, таблица</a></li>
                <li><a href="disk_exchange.jsp">Обмен дисков</a></li>
                <li><a href="exchange_history.jsp">История обменов пользователей</a></li>
                <li><a href="reports.jsp">Отчёты</a></li>
            </ul>
        </div>
    </div>
</main>
</body>
</html>
