<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.User"%>
<%@page import="models.MediaReceipt"%>
<%@page import="models.MediaSale"%>
<%@page import="dao.MediaReceiptDAO"%>
<%@page import="dao.MediaSaleDAO"%>
<%@page import="java.util.List"%>

<%
    request.setCharacterEncoding("UTF-8");
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    MediaReceiptDAO receiptDAO = new MediaReceiptDAO();
    MediaSaleDAO saleDAO = new MediaSaleDAO();

    List<MediaReceipt> receiptsWithStock = receiptDAO.getReceiptsWithAvailableStock(user.getId());
    List<MediaSale> sales = saleDAO.getSalesByUserId(user.getId());
%>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Продажа носителей — MiniSite</title>
    <link rel="stylesheet" href="css/site.css">
</head>
<body class="app">
<%@ include file="includes/nav-auth.jspf" %>
<main class="main">
    <div class="container">
        <h1 class="page-title">Сведения о продаже дисков и кассет</h1>
        <p class="page-desc">Регистрация продажи и таблица по вашим операциям.</p>

        <%
            String error = request.getParameter("error");
            String success = request.getParameter("success");

            if (error != null) {
                if ("empty_fields".equals(error)) {
                    out.println("<div class='alert alert--error'>Укажите поступление, дату, количество и сумму.</div>");
                } else if ("invalid_qty".equals(error)) {
                    out.println("<div class='alert alert--error'>Количество должно быть не меньше 1.</div>");
                } else if ("invalid_amount".equals(error)) {
                    out.println("<div class='alert alert--error'>Сумма не может быть отрицательной.</div>");
                } else if ("invalid_receipt".equals(error)) {
                    out.println("<div class='alert alert--error'>Поступление не найдено или недоступно.</div>");
                } else if ("insufficient_stock".equals(error)) {
                    out.println("<div class='alert alert--error'>Недостаточно товара на остатке.</div>");
                } else if ("db_error".equals(error)) {
                    out.println("<div class='alert alert--error'>Ошибка сохранения. Попробуйте позже.</div>");
                }
            }
            if (success != null) {
                if ("created".equals(success)) {
                    out.println("<div class='alert alert--success'>Продажа зарегистрирована.</div>");
                } else if ("deleted".equals(success)) {
                    out.println("<div class='alert alert--success'>Запись удалена.</div>");
                }
            }
        %>

        <div class="card">
            <h2 class="section-title">Новая продажа</h2>
            <% if (receiptsWithStock.isEmpty()) { %>
                <p class="hint">Нет остатков — сначала зарегистрируйте <a href="media_receipts.jsp">поступление</a>.</p>
            <% } else { %>
            <form action="MediaSaleServlet" method="post">
                <div class="form-row">
                    <div class="form-group">
                        <label>Поступление (остаток) *</label>
                        <select name="mediaReceiptId" required>
                            <option value="">Выберите партию</option>
                            <% for (MediaReceipt r : receiptsWithStock) {
                                int stock = r.getAvailableStock() != null ? r.getAvailableStock() : 0;
                            %>
                                <option value="<%= r.getId() %>">
                                    <%= r.getTitle() %> — <%= r.getMediaTypeName() != null ? r.getMediaTypeName() : "" %>
                                    (доступно: <%= stock %>)
                                </option>
                            <% } %>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Дата продажи *</label>
                        <input type="date" name="saleDate" id="saleDate" required>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>Количество *</label>
                        <input type="number" name="quantitySold" min="1" value="1" required>
                    </div>
                    <div class="form-group">
                        <label>Сумма продажи (₽) *</label>
                        <input type="number" name="saleAmount" min="0" step="0.01" required placeholder="0.00">
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>Покупатель</label>
                        <input type="text" name="customerName" placeholder="Необязательно">
                    </div>
                    <div class="form-group">
                        <label>Телефон</label>
                        <input type="text" name="customerPhone" placeholder="Необязательно">
                    </div>
                </div>
                <div class="form-group">
                    <label>Примечания</label>
                    <textarea name="notes" placeholder="Необязательно"></textarea>
                </div>
                <button type="submit" class="btn btn--success">Зарегистрировать продажу</button>
            </form>
            <% } %>
        </div>

        <h2 class="section-title">Таблица продаж</h2>

        <%
            if (sales.isEmpty()) {
                out.println("<div class='empty-state'>Пока нет записей о продажах.</div>");
            } else {
        %>
        <div class="table-wrap">
        <table class="data-table">
            <thead>
                <tr>
                    <th>№</th>
                    <th>Дата</th>
                    <th>Название</th>
                    <th>Тип</th>
                    <th>Кол-во</th>
                    <th>Сумма</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <%
                    int n = 1;
                    for (MediaSale s : sales) {
                %>
                <tr>
                    <td><%= n++ %></td>
                    <td><%= s.getSaleDate() %></td>
                    <td><strong><%= s.getReceiptTitle() != null ? s.getReceiptTitle() : "—" %></strong></td>
                    <td><span class="badge"><%= s.getMediaTypeName() != null ? s.getMediaTypeName() : "—" %></span></td>
                    <td><%= s.getQuantitySold() %></td>
                    <td><%= String.format("%.2f ₽", s.getSaleAmount()) %></td>
                    <td>
                        <a href="MediaSaleServlet?action=delete&id=<%= s.getId() %>"
                           class="btn btn--danger"
                           onclick="return confirm('Удалить запись о продаже?')">Удалить</a>
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
    var el = document.getElementById('saleDate');
    if (el) el.value = new Date().toISOString().split('T')[0];
</script>
</body>
</html>
