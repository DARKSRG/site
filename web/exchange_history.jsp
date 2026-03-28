<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.User"%>
<%@page import="models.DiskExchange"%>
<%@page import="models.MediaType"%>
<%@page import="dao.DiskExchangeDAO"%>
<%@page import="dao.MediaTypeDAO"%>
<%@page import="dao.UserDAO"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Calendar"%>

<%
    request.setCharacterEncoding("UTF-8");
    User sessionUser = (User) session.getAttribute("user");
    if (sessionUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    Calendar cal = Calendar.getInstance();
    java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
    cal.set(Calendar.DAY_OF_MONTH, 1);
    java.sql.Date monthStart = new java.sql.Date(cal.getTimeInMillis());

    String pUser = request.getParameter("filterUserId");
    String pType = request.getParameter("filterMediaTypeId");
    String pStart = request.getParameter("startDate");
    String pEnd = request.getParameter("endDate");

    if (pStart == null || pStart.trim().isEmpty()) {
        pStart = monthStart.toString();
    }
    if (pEnd == null || pEnd.trim().isEmpty()) {
        pEnd = today.toString();
    }

    Integer filterUserId = null;
    if (pUser != null && !pUser.trim().isEmpty() && !"all".equals(pUser)) {
        try {
            filterUserId = Integer.valueOf(pUser.trim());
        } catch (NumberFormatException e) {
            filterUserId = null;
        }
    }

    Integer filterMediaTypeId = null;
    if (pType != null && !pType.trim().isEmpty() && !"all".equals(pType)) {
        try {
            filterMediaTypeId = Integer.valueOf(pType.trim());
        } catch (NumberFormatException e) {
            filterMediaTypeId = null;
        }
    }

    java.sql.Date dFrom = java.sql.Date.valueOf(pStart);
    java.sql.Date dTo = java.sql.Date.valueOf(pEnd);

    UserDAO userDAO = new UserDAO();
    MediaTypeDAO typeDAO = new MediaTypeDAO();
    DiskExchangeDAO exchangeDAO = new DiskExchangeDAO();

    List<User> allUsers = userDAO.getAllUsers();
    List<MediaType> mediaTypes = typeDAO.getAllTypes();
    List<DiskExchange> rows = exchangeDAO.searchExchanges(filterUserId, dFrom, dTo, filterMediaTypeId);
%>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>История обменов — MiniSite</title>
    <link rel="stylesheet" href="css/site.css">
</head>
<body class="app">
<%@ include file="includes/nav-auth.jspf" %>
<main class="main">
    <div class="container">
        <h1 class="page-title">История обмена пользователей</h1>
        <p class="page-desc">Фильтрация по пользователю, периоду даты обмена и типу носителя. По умолчанию — с 1-го числа месяца по сегодня.</p>

        <div class="card">
            <form method="get" action="exchange_history.jsp">
                <div class="filter-bar">
                    <div class="form-field">
                        <label for="filterUserId">Пользователь</label>
                        <select name="filterUserId" id="filterUserId">
                            <option value="all" <%= (pUser == null || "all".equals(pUser)) ? "selected" : "" %>>Все</option>
                            <% for (User u : allUsers) {
                                String sel = (pUser != null && pUser.equals(String.valueOf(u.getId()))) ? "selected" : "";
                            %>
                                <option value="<%= u.getId() %>" <%= sel %>><%= u.getUsername() %><%= u.getFullName() != null && !u.getFullName().isEmpty() ? " — " + u.getFullName() : "" %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="form-field">
                        <label for="startDate">Период с</label>
                        <input type="date" name="startDate" id="startDate" value="<%= pStart %>">
                    </div>
                    <div class="form-field">
                        <label for="endDate">по</label>
                        <input type="date" name="endDate" id="endDate" value="<%= pEnd %>">
                    </div>
                    <div class="form-field">
                        <label for="filterMediaTypeId">Тип носителя</label>
                        <select name="filterMediaTypeId" id="filterMediaTypeId">
                            <option value="all" <%= (pType == null || "all".equals(pType)) ? "selected" : "" %>>Все</option>
                            <% for (MediaType t : mediaTypes) {
                                String sel = (pType != null && pType.equals(String.valueOf(t.getId()))) ? "selected" : "";
                            %>
                                <option value="<%= t.getId() %>" <%= sel %>><%= t.getTypeName() %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="form-field">
                        <label>&nbsp;</label>
                        <button type="submit" class="btn btn--primary">Показать</button>
                    </div>
                </div>
            </form>
        </div>

        <p class="stat">Найдено записей: <strong><%= rows.size() %></strong></p>

        <% if (rows.isEmpty()) { %>
            <div class="empty-state">Нет обменов за выбранные условия.</div>
        <% } else { %>
        <div class="table-wrap">
        <table class="data-table">
            <thead>
                <tr>
                    <th>№</th>
                    <th>Пользователь</th>
                    <th>Тип</th>
                    <th>Название</th>
                    <th>Год</th>
                    <th>Состояние</th>
                    <th>Дата обмена</th>
                    <th>Клиент</th>
                    <th>Номер</th>
                    <th>На обмен</th>
                    <th>Новый диск</th>
                </tr>
            </thead>
            <tbody>
                <%
                    int n = 1;
                    for (DiskExchange ex : rows) {
                %>
                <tr>
                    <td><%= n++ %></td>
                    <td><%= ex.getOwnerLabel() %></td>
                    <td><span class="badge"><%= ex.getMediaTypeName() != null ? ex.getMediaTypeName() : "—" %></span></td>
                    <td><strong><%= ex.getTitle() %></strong></td>
                    <td><%= ex.getReleaseYear() != null ? ex.getReleaseYear() : "—" %></td>
                    <td><%= ex.getDiskCondition() != null ? ex.getDiskCondition() : "—" %></td>
                    <td><%= ex.getExchangeDate() %></td>
                    <td><%= ex.getClientInfo() != null ? ex.getClientInfo() : "—" %></td>
                    <td class="mono"><%= ex.getExchangeNumber() %></td>
                    <td><%= ex.getDiskForExchange() != null ? ex.getDiskForExchange() : "—" %></td>
                    <td><%= ex.getNewDisk() != null ? ex.getNewDisk() : "—" %></td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>
        </div>
        <% } %>
    </div>
</main>
</body>
</html>
