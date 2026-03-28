<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.User"%>
<%@page import="models.ClientCard"%>
<%@page import="dao.ClientCardDAO"%>
<%@page import="java.util.List"%>

<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    ClientCardDAO cardDAO = new ClientCardDAO();
    List<ClientCard> cards = cardDAO.getCardsByUserId(user.getId());
%>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Учётные карточки — MiniSite</title>
    <link rel="stylesheet" href="css/site.css">
</head>
<body class="app">
<%@ include file="includes/nav-auth.jspf" %>
<main class="main">
    <div class="container">
        <h1 class="page-title">Учётные карточки клиентов</h1>
        <p class="page-desc">Создание и просмотр карточек.</p>

        <%
            String error = request.getParameter("error");
            String success = request.getParameter("success");

            if (error != null) {
                if (error.equals("empty_fields")) {
                    out.println("<div class='alert alert--error'>Заполните все обязательные поля.</div>");
                } else if (error.equals("db_error")) {
                    out.println("<div class='alert alert--error'>Ошибка при сохранении. Попробуйте позже.</div>");
                }
            }
            if (success != null && success.equals("created")) {
                out.println("<div class='alert alert--success'>Карточка успешно создана.</div>");
            }
        %>

        <div class="card">
            <h2 class="section-title">Новая карточка</h2>
            <form action="ClientCardServlet" method="post">
                <div class="form-field-inline">
                    <label for="lastName">Фамилия *</label>
                    <input type="text" id="lastName" name="lastName" required style="max-width:320px;">
                </div>
                <div class="form-field-inline">
                    <label for="firstName">Имя *</label>
                    <input type="text" id="firstName" name="firstName" required style="max-width:320px;">
                </div>
                <div class="form-field-inline">
                    <label for="middleName">Отчество</label>
                    <input type="text" id="middleName" name="middleName" style="max-width:320px;">
                </div>
                <div class="form-field-inline">
                    <label for="registrationDate">Дата регистрации *</label>
                    <input type="date" id="registrationDate" name="registrationDate" required style="max-width:320px;">
                </div>
                <button type="submit" class="btn btn--success" style="margin-left:0;">Создать карточку</button>
            </form>
        </div>

        <h2 class="section-title">Список карточек</h2>

        <%
            if (cards.isEmpty()) {
                out.println("<div class='empty-state'>Пока нет карточек. Создайте первую.</div>");
            } else {
        %>
        <div class="table-wrap">
        <table class="data-table">
            <thead>
                <tr>
                    <th>№</th>
                    <th>Номер</th>
                    <th>Фамилия</th>
                    <th>Имя</th>
                    <th>Отчество</th>
                    <th>Дата регистрации</th>
                    <th>Создано</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <%
                    int count = 1;
                    for (ClientCard card : cards) {
                %>
                <tr>
                    <td><%= count++ %></td>
                    <td class="mono"><%= card.getCardNumber() %></td>
                    <td><%= card.getLastName() %></td>
                    <td><%= card.getFirstName() %></td>
                    <td><%= card.getMiddleName() != null ? card.getMiddleName() : "—" %></td>
                    <td><%= card.getRegistrationDate() %></td>
                    <td><%= card.getCreatedAt() %></td>
                    <td>
                        <a href="ClientCardServlet?action=delete&id=<%= card.getId() %>"
                           class="btn btn--danger"
                           onclick="return confirm('Удалить карточку <%= card.getCardNumber() %>?')">Удалить</a>
                    </td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>
        </div>
        <%
            }
        %>
    </div>
</main>
<script>
    document.getElementById('registrationDate').value = new Date().toISOString().split('T')[0];
</script>
</body>
</html>
