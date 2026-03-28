package controllers;

import dao.MediaReceiptDAO;
import dao.MediaTypeDAO;
import models.MediaReceipt;
import models.MediaType;
import models.ReportFilter;
import models.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ReportServlet")
public class ReportServlet extends HttpServlet {
    
    private MediaReceiptDAO receiptDAO = new MediaReceiptDAO();
    private MediaTypeDAO typeDAO = new MediaTypeDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");
        
        if ("generate".equals(action)) {
            generateReport(request, response, user);
        } else {
            response.sendRedirect("reports.jsp");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    private void generateReport(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        
        // Получаем параметры фильтрации
        ReportFilter filter = new ReportFilter();
        filter.setReportType(request.getParameter("reportType"));
        
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        
        if (startDateStr != null && !startDateStr.isEmpty()) {
            filter.setStartDate(Date.valueOf(startDateStr));
        }
        if (endDateStr != null && !endDateStr.isEmpty()) {
            filter.setEndDate(Date.valueOf(endDateStr));
        }
        
        String mediaTypeIdStr = request.getParameter("mediaTypeId");
        if (mediaTypeIdStr != null && !mediaTypeIdStr.isEmpty()) {
            filter.setMediaTypeId(Integer.parseInt(mediaTypeIdStr));
        }
        
        filter.setBatchNumber(request.getParameter("batchNumber"));
        filter.setFormat(request.getParameter("format"));
        
        // Получаем данные для отчета
        List<MediaReceipt> receipts = receiptDAO.getReceiptsByUserId(user.getId());
        
        // Фильтруем данные
        receipts = filterReceipts(receipts, filter);
        
        // Формируем отчет в выбранном формате
        String format = filter.getFormat();
        if ("excel".equals(format)) {
            generateExcelReport(response, receipts, filter);
        } else if ("pdf".equals(format)) {
            generatePdfReport(response, receipts, filter);
        } else {
            generateHtmlReport(response, receipts, filter);
        }
    }
    
    private List<MediaReceipt> filterReceipts(List<MediaReceipt> receipts, ReportFilter filter) {
        return receipts.stream()
            .filter(r -> {
                // Фильтр по дате
                if (filter.getStartDate() != null && r.getReceiptDate().before(filter.getStartDate())) {
                    return false;
                }
                if (filter.getEndDate() != null && r.getReceiptDate().after(filter.getEndDate())) {
                    return false;
                }
                // Фильтр по типу носителя
                if (filter.getMediaTypeId() != null && r.getMediaTypeId() != filter.getMediaTypeId()) {
                    return false;
                }
                // Фильтр по номеру партии
                if (filter.getBatchNumber() != null && !filter.getBatchNumber().isEmpty()) {
                    if (r.getBatchNumber() == null || !r.getBatchNumber().contains(filter.getBatchNumber())) {
                        return false;
                    }
                }
                return true;
            })
            .collect(java.util.stream.Collectors.toList());
    }
    
    private void generateHtmlReport(HttpServletResponse response, List<MediaReceipt> receipts, ReportFilter filter) 
            throws IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Отчет по поступлениям</title>");
        out.println("<style>");
        out.println("body { font-family: Arial; margin: 40px; }");
        out.println("h1 { color: #333; }");
        out.println(".report-info { background: #f0f0f0; padding: 15px; border-radius: 5px; margin-bottom: 20px; }");
        out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        out.println("th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }");
        out.println("th { background: #007bff; color: white; }");
        out.println("tr:nth-child(even) { background: #f9f9f9; }");
        out.println(".total { margin-top: 20px; font-weight: bold; text-align: right; }");
        out.println(".print-btn { margin: 20px 0; padding: 10px 20px; background: #28a745; color: white; border: none; cursor: pointer; border-radius: 5px; }");
        out.println(".print-btn:hover { background: #218838; }");
        out.println("@media print { .no-print { display: none; } }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        out.println("<button class='print-btn no-print' onclick='window.print()'>🖨️ Печать отчета</button>");
        out.println("<button class='print-btn no-print' onclick='window.history.back()'>← Назад</button>");
        
        out.println("<h1>Отчет по поступлениям дисков/кассет</h1>");
        
        // Информация о фильтрах
        out.println("<div class='report-info'>");
        out.println("<h3>Параметры отчета:</h3>");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        
        if (filter.getStartDate() != null || filter.getEndDate() != null) {
            out.println("<p><strong>Период:</strong> ");
            if (filter.getStartDate() != null) out.println("с " + sdf.format(filter.getStartDate()));
            if (filter.getEndDate() != null) out.println(" по " + sdf.format(filter.getEndDate()));
            out.println("</p>");
        }
        
        if (filter.getMediaTypeId() != null) {
            MediaType type = typeDAO.getTypeById(filter.getMediaTypeId());
            out.println("<p><strong>Тип носителя:</strong> " + (type != null ? type.getTypeName() : "") + "</p>");
        }
        
        if (filter.getBatchNumber() != null && !filter.getBatchNumber().isEmpty()) {
            out.println("<p><strong>Номер партии:</strong> " + filter.getBatchNumber() + "</p>");
        }
        
        out.println("<p><strong>Дата формирования:</strong> " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new java.util.Date()) + "</p>");
        out.println("</div>");
        
        // Таблица с данными
        if (receipts.isEmpty()) {
            out.println("<p style='color: red;'>Нет данных для отображения по выбранным критериям.</p>");
        } else {
            out.println("<table>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>№</th>");
            out.println("<th>Дата</th>");
            out.println("<th>Тип</th>");
            out.println("<th>Название</th>");
            out.println("<th>Год</th>");
            out.println("<th>Кол-во</th>");
            out.println("<th>Документ</th>");
            out.println("<th>Партия</th>");
            out.println("<th>Поставщик</th>");
            out.println("<th>Сумма</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");
            
            int count = 1;
            double totalSum = 0;
            int totalQuantity = 0;
            
            for (MediaReceipt r : receipts) {
                totalSum += r.getTotalAmount() != null ? r.getTotalAmount() : 0;
                totalQuantity += r.getQuantity();
                
                out.println("<tr>");
                out.println("<td>" + count++ + "</td>");
                out.println("<td>" + r.getReceiptDate() + "</td>");
                out.println("<td>" + r.getMediaTypeName() + "</td>");
                out.println("<td>" + r.getTitle() + "</td>");
                out.println("<td>" + (r.getReleaseYear() != null ? r.getReleaseYear() : "-") + "</td>");
                out.println("<td>" + r.getQuantity() + "</td>");
                out.println("<td>" + r.getDocumentNumber() + "</td>");
                out.println("<td>" + (r.getBatchNumber() != null ? r.getBatchNumber() : "-") + "</td>");
                out.println("<td>" + (r.getSupplierName() != null ? r.getSupplierName() : 
                                       (r.getSupplierInfo() != null ? r.getSupplierInfo() : "-")) + "</td>");
                out.println("<td>" + (r.getTotalAmount() != null ? String.format("%.2f руб.", r.getTotalAmount()) : "-") + "</td>");
                out.println("</tr>");
            }
            
            out.println("</tbody>");
            out.println("</table>");
            
            out.println("<div class='total'>");
            out.println("<p>📊 ИТОГО:</p>");
            out.println("<p>Количество позиций: " + receipts.size() + "</p>");
            out.println("<p>Общее количество единиц: " + totalQuantity + "</p>");
            out.println("<p>Общая сумма: " + String.format("%.2f руб.", totalSum) + "</p>");
            out.println("</div>");
        }
        
        out.println("</body>");
        out.println("</html>");
    }
    
    private void generateExcelReport(HttpServletResponse response, List<MediaReceipt> receipts, ReportFilter filter) 
            throws IOException {
        
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=report.xls");
        
        PrintWriter out = response.getWriter();
        
        out.println("<table border='1'>");
        out.println("<tr>");
        out.println("<th>№</th>");
        out.println("<th>Дата</th>");
        out.println("<th>Тип</th>");
        out.println("<th>Название</th>");
        out.println("<th>Год</th>");
        out.println("<th>Кол-во</th>");
        out.println("<th>Документ</th>");
        out.println("<th>Партия</th>");
        out.println("<th>Поставщик</th>");
        out.println("<th>Сумма</th>");
        out.println("</tr>");
        
        int count = 1;
        double totalSum = 0;
        int totalQuantity = 0;
        
        for (MediaReceipt r : receipts) {
            totalSum += r.getTotalAmount() != null ? r.getTotalAmount() : 0;
            totalQuantity += r.getQuantity();
            
            out.println("<tr>");
            out.println("<td>" + count++ + "</td>");
            out.println("<td>" + r.getReceiptDate() + "</td>");
            out.println("<td>" + r.getMediaTypeName() + "</td>");
            out.println("<td>" + r.getTitle() + "</td>");
            out.println("<td>" + (r.getReleaseYear() != null ? r.getReleaseYear() : "-") + "</td>");
            out.println("<td>" + r.getQuantity() + "</td>");
            out.println("<td>" + r.getDocumentNumber() + "</td>");
            out.println("<td>" + (r.getBatchNumber() != null ? r.getBatchNumber() : "-") + "</td>");
            out.println("<td>" + (r.getSupplierName() != null ? r.getSupplierName() : 
                                   (r.getSupplierInfo() != null ? r.getSupplierInfo() : "-")) + "</td>");
            out.println("<td>" + (r.getTotalAmount() != null ? String.format("%.2f", r.getTotalAmount()) : "-") + "</td>");
            out.println("</tr>");
        }
        
        out.println("<tr style='font-weight:bold'>");
        out.println("<td colspan='5'>ИТОГО:</td>");
        out.println("<td>" + totalQuantity + "</td>");
        out.println("<td colspan='3'></td>");
        out.println("<td>" + String.format("%.2f", totalSum) + "</td>");
        out.println("</tr>");
        
        out.println("</table>");
    }
    
    private void generatePdfReport(HttpServletResponse response, List<MediaReceipt> receipts, ReportFilter filter) {
        // Для PDF нужна дополнительная библиотека (iText или Apache PDFBox)
        // Пока просто перенаправляем на HTML
        try {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>PDF формат требует установки дополнительной библиотеки</h2>");
            out.println("<p>Для генерации PDF рекомендуется использовать HTML-отчет с печатью.</p>");
            out.println("<a href='javascript:history.back()'>Назад</a>");
            out.println("</body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}