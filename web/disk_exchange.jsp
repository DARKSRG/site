<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.User"%>
<%@page import="models.DiskExchange"%>
<%@page import="models.MediaType"%>
<%@page import="dao.DiskExchangeDAO"%>
<%@page import="dao.MediaTypeDAO"%>
<%@page import="java.util.List"%>

<%
    request.setCharacterEncoding("UTF-8");
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    DiskExchangeDAO exchangeDAO = new DiskExchangeDAO();
    MediaTypeDAO typeDAO = new MediaTypeDAO();
    List<DiskExchange> exchanges = exchangeDAO.getExchangesByUserId(user.getId());
    List<MediaType> mediaTypes = typeDAO.getAllTypes();
%>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Обмен дисков — MiniSite</title>
    <link rel="stylesheet" href="css/site.css">
</head>
<body class="app">
<%@ include file="includes/nav-auth.jspf" %>
<main class="main">
    <div class="container">
        <h1 class="page-title">Обмен дисков</h1>
        <p class="page-desc">Учёт обмена носителей: что сдано и что выдано.</p>

        <%
            String error = request.getParameter("error");
            String success = request.getParameter("success");
            if ("empty_fields".equals(error)) {
                out.println("<div class='alert alert--error'>Укажите тип носителя, название и дату обмена.</div>");
            } else if ("db_error".equals(error)) {
                out.println("<div class='alert alert--error'>Ошибка сохранения. Проверьте номер обмена и таблицу в БД.</div>");
            }
            if ("created".equals(success)) {
                out.println("<div class='alert alert--success'>Запись добавлена.</div>");
            } else if ("deleted".equals(success)) {
                out.println("<div class='alert alert--success'>Запись удалена.</div>");
            }
        %>

        <div class="card">
            <h2 class="section-title">Новая запись</h2>
            <form action="DiskExchangeServlet" method="post">
                <div class="form-row">
                    <div class="form-group">
                        <label>Тип носителя *</label>
                        <select name="mediaTypeId" required>
                            <option value="">Выберите</option>
                            <% for (MediaType t : mediaTypes) { %>
                                <option value="<%= t.getId() %>"><%= t.getTypeName() %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Название *</label>
                        <input type="text" name="title" required placeholder="Фильм, альбом…">
                    </div>
                    <div class="form-group">
                        <label>Год выпуска</label>
                        <input type="number" name="releaseYear" min="1900" max="2100" placeholder="необязательно">
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>Состояние диска</label>
                        <input type="text" name="diskCondition" placeholder="Отличное, царапины…">
                    </div>
                    <div class="form-group">
                        <label>Дата обмена *</label>
                        <input type="date" name="exchangeDate" id="exchangeDate" required>
                    </div>
                    <div class="form-group">
                        <label>Номер обмена</label>
                        <input type="text" name="exchangeNumber" placeholder="Пусто — авто">
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group wide">
                        <label>Сведения о клиенте</label>
                        <textarea name="clientInfo" placeholder="ФИО, контакт"></textarea>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group wide">
                        <label>Диск на обмен</label>
                        <textarea name="diskForExchange" placeholder="Что сдаёт клиент"></textarea>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group wide">
                        <label>Новый диск</label>
                        <textarea name="newDisk" placeholder="Что выдаётся взамен"></textarea>
                    </div>
                </div>
                <button type="submit" class="btn btn--primary">Сохранить</button>
            </form>
        </div>

        <h2 class="section-title">Журнал обменов</h2>

        <%
            if (exchanges.isEmpty()) {
                out.println("<div class='empty-state'>Пока нет записей.</div>");
            } else {
        %>
        <div class="table-wrap">
        <table class="data-table">
            <thead>
                <tr>
                    <th>№</th>
                    <th>Тип</th>
                    <th>Название</th>
                    <th>Год</th>
                    <th>Состояние</th>
                    <th>Дата</th>
                    <th>Клиент</th>
                    <th>Номер</th>
                    <th>На обмен</th>
                    <th>Новый</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <%
                    int n = 1;
                    for (DiskExchange ex : exchanges) {
                %>
                <tr>
                    <td><%= n++ %></td>
                    <td><span class="badge"><%= ex.getMediaTypeName() != null ? ex.getMediaTypeName() : "—" %></span></td>
                    <td><strong><%= ex.getTitle() %></strong></td>
                    <td><%= ex.getReleaseYear() != null ? ex.getReleaseYear() : "—" %></td>
                    <td><%= ex.getDiskCondition() != null ? ex.getDiskCondition() : "—" %></td>
                    <td><%= ex.getExchangeDate() %></td>
                    <td><%= ex.getClientInfo() != null ? ex.getClientInfo() : "—" %></td>
                    <td class="mono"><%= ex.getExchangeNumber() %></td>
                    <td><%= ex.getDiskForExchange() != null ? ex.getDiskForExchange() : "—" %></td>
                    <td><%= ex.getNewDisk() != null ? ex.getNewDisk() : "—" %></td>
                    <td>
                        <a href="DiskExchangeServlet?action=delete&id=<%= ex.getId() %>"
                           class="btn btn--danger"
                           onclick="return confirm('Удалить запись?')">Удалить</a>
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
    var el = document.getElementById('exchangeDate');
    if (el && !el.value) el.value = new Date().toISOString().split('T')[0];
</script>
</body>
</html>
