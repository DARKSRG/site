package controllers;

import dao.MediaReceiptDAO;
import models.MediaReceipt;
import models.User;
import java.io.IOException;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/MediaReceiptServlet")
public class MediaReceiptServlet extends HttpServlet {
    
    private MediaReceiptDAO receiptDAO = new MediaReceiptDAO();
    
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
        
        if ("delete".equals(action)) {
            int receiptId = Integer.parseInt(request.getParameter("id"));
            receiptDAO.deleteReceipt(receiptId, user.getId());
            response.sendRedirect("media_receipts.jsp?success=deleted");
        } else {
            response.sendRedirect("media_receipts.jsp");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        // Получаем параметры из формы
        int mediaTypeId = Integer.parseInt(request.getParameter("mediaTypeId"));
        String title = request.getParameter("title");
        String releaseYearStr = request.getParameter("releaseYear");
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String receiptDate = request.getParameter("receiptDate");
        String documentNumber = request.getParameter("documentNumber");
        String supplierIdStr = request.getParameter("supplierId");
        String supplierInfo = request.getParameter("supplierInfo");
        String batchNumber = request.getParameter("batchNumber");
        String totalAmountStr = request.getParameter("totalAmount");
        String notes = request.getParameter("notes");
        
        // Валидация
        if (title == null || title.trim().isEmpty() ||
            receiptDate == null || receiptDate.trim().isEmpty() ||
            documentNumber == null || documentNumber.trim().isEmpty()) {
            
            response.sendRedirect("media_receipts.jsp?error=empty_fields");
            return;
        }
        
        // Создаем запись
        MediaReceipt receipt = new MediaReceipt();
        receipt.setUserId(user.getId());
        receipt.setMediaTypeId(mediaTypeId);
        receipt.setTitle(title);
        
        if (releaseYearStr != null && !releaseYearStr.isEmpty()) {
            receipt.setReleaseYear(Integer.parseInt(releaseYearStr));
        }
        
        receipt.setQuantity(quantity);
        receipt.setReceiptDate(Date.valueOf(receiptDate));
        receipt.setDocumentNumber(documentNumber);
        
        if (supplierIdStr != null && !supplierIdStr.isEmpty()) {
            receipt.setSupplierId(Integer.parseInt(supplierIdStr));
        }
        
        receipt.setSupplierInfo(supplierInfo);
        receipt.setBatchNumber(batchNumber);
        
        if (totalAmountStr != null && !totalAmountStr.isEmpty()) {
            receipt.setTotalAmount(Double.parseDouble(totalAmountStr));
        }
        
        receipt.setNotes(notes);
        
        // Сохраняем
        boolean success = receiptDAO.createReceipt(receipt);
        
        if (success) {
            response.sendRedirect("media_receipts.jsp?success=created");
        } else {
            response.sendRedirect("media_receipts.jsp?error=db_error");
        }
    }
}