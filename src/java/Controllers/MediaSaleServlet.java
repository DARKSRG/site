package controllers;

import dao.MediaReceiptDAO;
import dao.MediaSaleDAO;
import models.MediaSale;
import models.User;
import java.io.IOException;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/MediaSaleServlet")
public class MediaSaleServlet extends HttpServlet {

    private final MediaSaleDAO saleDAO = new MediaSaleDAO();
    private final MediaReceiptDAO receiptDAO = new MediaReceiptDAO();

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
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.isEmpty()) {
                int saleId = Integer.parseInt(idStr);
                saleDAO.deleteSale(saleId, user.getId());
            }
            response.sendRedirect("media_sales.jsp?success=deleted");
            return;
        }

        response.sendRedirect("media_sales.jsp");
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

        String receiptIdStr = request.getParameter("mediaReceiptId");
        String saleDateStr = request.getParameter("saleDate");
        String quantityStr = request.getParameter("quantitySold");
        String saleAmountStr = request.getParameter("saleAmount");

        if (receiptIdStr == null || receiptIdStr.trim().isEmpty()
                || saleDateStr == null || saleDateStr.trim().isEmpty()
                || quantityStr == null || quantityStr.trim().isEmpty()
                || saleAmountStr == null || saleAmountStr.trim().isEmpty()) {
            response.sendRedirect("media_sales.jsp?error=empty_fields");
            return;
        }

        int mediaReceiptId = Integer.parseInt(receiptIdStr);
        int quantitySold = Integer.parseInt(quantityStr);
        double saleAmount = Double.parseDouble(saleAmountStr.replace(',', '.'));

        if (quantitySold < 1) {
            response.sendRedirect("media_sales.jsp?error=invalid_qty");
            return;
        }
        if (saleAmount < 0) {
            response.sendRedirect("media_sales.jsp?error=invalid_amount");
            return;
        }

        int available = receiptDAO.getAvailableStock(mediaReceiptId, user.getId());
        if (available < 0) {
            response.sendRedirect("media_sales.jsp?error=invalid_receipt");
            return;
        }
        if (quantitySold > available) {
            response.sendRedirect("media_sales.jsp?error=insufficient_stock");
            return;
        }

        MediaSale sale = new MediaSale();
        sale.setUserId(user.getId());
        sale.setMediaReceiptId(mediaReceiptId);
        sale.setSaleDate(Date.valueOf(saleDateStr));
        sale.setQuantitySold(quantitySold);
        sale.setSaleAmount(saleAmount);
        sale.setUnitPrice(saleAmount / quantitySold);

        String customerName = request.getParameter("customerName");
        String customerPhone = request.getParameter("customerPhone");
        String notes = request.getParameter("notes");
        if (customerName != null && !customerName.trim().isEmpty()) {
            sale.setCustomerName(customerName.trim());
        }
        if (customerPhone != null && !customerPhone.trim().isEmpty()) {
            sale.setCustomerPhone(customerPhone.trim());
        }
        if (notes != null && !notes.trim().isEmpty()) {
            sale.setNotes(notes.trim());
        }

        boolean ok = saleDAO.createSale(sale);
        if (ok) {
            response.sendRedirect("media_sales.jsp?success=created");
        } else {
            response.sendRedirect("media_sales.jsp?error=db_error");
        }
    }
}
