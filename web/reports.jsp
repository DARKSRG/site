<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.User"%>
<%@page import="models.MediaType"%>
<%@page import="dao.MediaTypeDAO"%>
<%@page import="java.util.List"%>

<%
    request.setCharacterEncoding("UTF-8");
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    MediaTypeDAO typeDAO = new MediaTypeDAO();
    List<MediaType> mediaTypes = typeDAO.getAllTypes();
%>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Отчёты — MiniSite</title>
    <link rel="stylesheet" href="css/site.css">
    <script>
        function toggleDateRange() {
            var reportType = document.querySelector('input[name="reportType"]:checked').value;
            document.getElementById("dateRangeDiv").style.display = reportType === "by_date" ? "block" : "none";
        }
        function toggleMediaType() {
            var reportType = document.querySelector('input[name="reportType"]:checked').value;
            document.getElementById("mediaTypeDiv").style.display = reportType === "by_media_type" ? "block" : "none";
        }
        function toggleBatchNumber() {
            var reportType = document.querySelector('input[name="reportType"]:checked').value;
            document.getElementById("batchDiv").style.display = reportType === "by_batch" ? "block" : "none";
        }
        function updateFilters() {
            toggleDateRange();
            toggleMediaType();
            toggleBatchNumber();
        }
    </script>
</head>
<body class="app">
<%@ include file="includes/nav-auth.jspf" %>
<main class="main">
    <div class="container container--narrow">
        <h1 class="page-title">Формирование отчётов</h1>
        <p class="page-desc">Выберите тип отчёта, при необходимости уточните фильтры и формат.</p>

        <div class="alert alert--info">
            <strong>Как пользоваться:</strong> укажите тип отчёта → параметры → формат → «Сформировать отчёт».
        </div>

        <div class="card">
            <form action="ReportServlet" method="get">
                <input type="hidden" name="action" value="generate">

                <div class="form-field mb-2">
                    <span class="label">Тип отчёта</span>
                    <div class="radio-row">
                        <label><input type="radio" name="reportType" value="all" checked onclick="updateFilters()"> Все поступления</label>
                        <label><input type="radio" name="reportType" value="by_date" onclick="updateFilters()"> По периоду</label>
                        <label><input type="radio" name="reportType" value="by_media_type" onclick="updateFilters()"> По типу носителя</label>
                        <label><input type="radio" name="reportType" value="by_batch" onclick="updateFilters()"> По номеру партии</label>
                    </div>
                </div>

                <div id="dateRangeDiv" style="display: none;">
                    <div class="form-row">
                        <div class="form-group">
                            <label>Дата с</label>
                            <input type="date" name="startDate">
                        </div>
                        <div class="form-group">
                            <label>Дата по</label>
                            <input type="date" name="endDate">
                        </div>
                    </div>
                </div>

                <div id="mediaTypeDiv" style="display: none;">
                    <div class="form-field mb-2">
                        <label for="mediaTypeId">Тип носителя</label>
                        <select name="mediaTypeId" id="mediaTypeId">
                            <option value="">Все типы</option>
                            <% for (MediaType type : mediaTypes) { %>
                                <option value="<%= type.getId() %>"><%= type.getTypeName() %></option>
                            <% } %>
                        </select>
                    </div>
                </div>

                <div id="batchDiv" style="display: none;">
                    <div class="form-field mb-2">
                        <label for="batchNumber">Номер партии (или часть)</label>
                        <input type="text" name="batchNumber" id="batchNumber" placeholder="Например: 001">
                    </div>
                </div>

                <hr class="hr">

                <div class="form-field mb-2">
                    <span class="label">Формат</span>
                    <div class="radio-row">
                        <label><input type="radio" name="format" value="html" checked> HTML (просмотр и печать)</label>
                        <label><input type="radio" name="format" value="excel"> Excel (XLS)</label>
                        <label><input type="radio" name="format" value="pdf"> PDF (если подключена библиотека)</label>
                    </div>
                </div>

                <button type="submit" class="btn btn--primary">Сформировать отчёт</button>
            </form>
        </div>

        <div class="alert alert--hint">
            HTML можно распечатать (Ctrl+P) или сохранить как PDF из браузера. Excel загрузится файлом.
        </div>
    </div>
</main>
<script>
    var today = new Date();
    var lastMonth = new Date();
    lastMonth.setMonth(today.getMonth() - 1);
    document.querySelector('input[name="startDate"]').value = lastMonth.toISOString().split('T')[0];
    document.querySelector('input[name="endDate"]').value = today.toISOString().split('T')[0];
</script>
</body>
</html>
