<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.User"%>
<%@page import="models.MediaReceipt"%>
<%@page import="models.MediaType"%>
<%@page import="models.Supplier"%>
<%@page import="dao.MediaReceiptDAO"%>
<%@page import="dao.MediaTypeDAO"%>
<%@page import="dao.SupplierDAO"%>
<%@page import="java.util.List"%>

<%
    request.setCharacterEncoding("UTF-8");
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    MediaReceiptDAO receiptDAO = new MediaReceiptDAO();
    MediaTypeDAO typeDAO = new MediaTypeDAO();
    SupplierDAO supplierDAO = new SupplierDAO();

    List<MediaReceipt> receipts = receiptDAO.getReceiptsByUserId(user.getId());
    List<MediaType> mediaTypes = typeDAO.getAllTypes();
    List<Supplier> suppliers = supplierDAO.getAllSuppliers();
%>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Поступление носителей — MiniSite</title>
    <link rel="stylesheet" href="css/site.css">
    <script>
        function toggleSupplierFields() {
            var select = document.getElementById("supplierId");
            var customDiv = document.getElementById("customSupplierDiv");
            customDiv.style.display = select.value === "" ? "block" : "none";
        }
    </script>
</head>
<body class="app">
<%@ include file="includes/nav-auth.jspf" %>
<main class="main">
    <div class="container">
        <h1 class="page-title">Поступление дисков и кассет</h1>
        <p class="page-desc">Регистрация поступления и история.</p>

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
            if (success != null) {
                if (success.equals("created")) {
                    out.println("<div class='alert alert--success'>Поступление зарегистрировано.</div>");
                } else if (success.equals("deleted")) {
                    out.println("<div class='alert alert--success'>Запись удалена.</div>");
                }
            }
        %>

        <div class="card">
            <h2 class="section-title">Новое поступление</h2>
            <form action="MediaReceiptServlet" method="post">
                <div class="form-row">
                    <div class="form-group">
                        <label>Тип носителя *</label>
                        <select name="mediaTypeId" required>
                            <option value="">Выберите тип</option>
                            <% for (MediaType type : mediaTypes) { %>
                                <option value="<%= type.getId() %>"><%= type.getTypeName() %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Название *</label>
                        <input type="text" name="title" required>
                    </div>
                    <div class="form-group">
                        <label>Год выпуска</label>
                        <input type="number" name="releaseYear" min="1900" max="2100">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Количество *</label>
                        <input type="number" name="quantity" min="1" value="1" required>
                    </div>
                    <div class="form-group">
                        <label>Дата поступления *</label>
                        <input type="date" name="receiptDate" required>
                    </div>
                    <div class="form-group">
                        <label>Номер документа *</label>
                        <input type="text" name="documentNumber" placeholder="Накладная №…" required>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Номер партии</label>
                        <input type="text" name="batchNumber" placeholder="Партия №">
                    </div>
                    <div class="form-group">
                        <label>Сумма поступления (руб.)</label>
                        <input type="number" name="totalAmount" step="0.01" placeholder="0.00">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Поставщик</label>
                        <select name="supplierId" id="supplierId" onchange="toggleSupplierFields()">
                            <option value="">Выберите или укажите вручную</option>
                            <% for (Supplier sup : suppliers) { %>
                                <option value="<%= sup.getId() %>"><%= sup.getSupplierName() %></option>
                            <% } %>
                            <option value="new">+ Новый поставщик</option>
                        </select>
                    </div>
                </div>

                <div id="customSupplierDiv" style="display:none;">
                    <div class="supplier-section">
                        <h4>Новый поставщик</h4>
                        <div class="form-row">
                            <div class="form-group">
                                <label>Наименование</label>
                                <input type="text" name="supplierInfo" placeholder="ООО …">
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>Контакт</label>
                                <input type="text" name="supplierContact" placeholder="ФИО">
                            </div>
                            <div class="form-group">
                                <label>Телефон</label>
                                <input type="text" name="supplierPhone" placeholder="+7 …">
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>Email</label>
                                <input type="email" name="supplierEmail" placeholder="email@…">
                            </div>
                            <div class="form-group">
                                <label>Адрес</label>
                                <input type="text" name="supplierAddress" placeholder="Город, улица">
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label>Примечания</label>
                    <textarea name="notes" placeholder="Дополнительно"></textarea>
                </div>

                <button type="submit" class="btn btn--success">Зарегистрировать поступление</button>
            </form>
        </div>

        <h2 class="section-title">История поступлений</h2>

        <%
            if (receipts.isEmpty()) {
                out.println("<div class='empty-state'>Нет зарегистрированных поступлений.</div>");
            } else {
        %>
        <div class="table-wrap">
        <table class="data-table">
            <thead>
                <tr>
                    <th>№</th>
                    <th>Дата</th>
                    <th>Тип</th>
                    <th>Название</th>
                    <th>Год</th>
                    <th>Кол-во</th>
                    <th>Документ</th>
                    <th>Партия</th>
                    <th>Поставщик</th>
                    <th>Сумма</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <%
                    int count = 1;
                    for (MediaReceipt receipt : receipts) {
                %>
                <tr>
                    <td><%= count++ %></td>
                    <td><%= receipt.getReceiptDate() %></td>
                    <td><span class="badge"><%= receipt.getMediaTypeName() %></span></td>
                    <td><strong><%= receipt.getTitle() %></strong></td>
                    <td><%= receipt.getReleaseYear() != null ? receipt.getReleaseYear() : "—" %></td>
                    <td><%= receipt.getQuantity() %></td>
                    <td><%= receipt.getDocumentNumber() %></td>
                    <td><%= receipt.getBatchNumber() != null ? receipt.getBatchNumber() : "—" %></td>
                    <td>
                        <%= receipt.getSupplierName() != null ? receipt.getSupplierName() :
                           (receipt.getSupplierInfo() != null ? receipt.getSupplierInfo() : "—") %>
                    </td>
                    <td><%= receipt.getTotalAmount() != null ? String.format("%.2f ₽", receipt.getTotalAmount()) : "—" %></td>
                    <td>
                        <a href="MediaReceiptServlet?action=delete&id=<%= receipt.getId() %>"
                           class="btn btn--danger"
                           onclick="return confirm('Удалить запись о поступлении?')">Удалить</a>
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
    document.querySelector('input[name="receiptDate"]').value = new Date().toISOString().split('T')[0];
</script>
</body>
</html>
