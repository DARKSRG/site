package controllers;

import dao.DiskExchangeDAO;
import models.DiskExchange;
import models.User;
import java.io.IOException;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/DiskExchangeServlet")
public class DiskExchangeServlet extends HttpServlet {

    private final DiskExchangeDAO dao = new DiskExchangeDAO();

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
                dao.deleteExchange(Integer.parseInt(idStr), user.getId());
            }
            response.sendRedirect("disk_exchange.jsp?success=deleted");
            return;
        }

        response.sendRedirect("disk_exchange.jsp");
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

        String mediaTypeIdStr = request.getParameter("mediaTypeId");
        String title = request.getParameter("title");
        String releaseYearStr = request.getParameter("releaseYear");
        String diskCondition = request.getParameter("diskCondition");
        String exchangeDateStr = request.getParameter("exchangeDate");
        String clientInfo = request.getParameter("clientInfo");
        String exchangeNumber = request.getParameter("exchangeNumber");
        String diskForExchange = request.getParameter("diskForExchange");
        String newDisk = request.getParameter("newDisk");

        if (mediaTypeIdStr == null || mediaTypeIdStr.trim().isEmpty()
                || title == null || title.trim().isEmpty()
                || exchangeDateStr == null || exchangeDateStr.trim().isEmpty()) {
            response.sendRedirect("disk_exchange.jsp?error=empty_fields");
            return;
        }

        DiskExchange ex = new DiskExchange();
        ex.setUserId(user.getId());
        ex.setMediaTypeId(Integer.parseInt(mediaTypeIdStr));
        ex.setTitle(title.trim());
        if (releaseYearStr != null && !releaseYearStr.trim().isEmpty()) {
            ex.setReleaseYear(Integer.parseInt(releaseYearStr.trim()));
        }
        if (diskCondition != null && !diskCondition.trim().isEmpty()) {
            ex.setDiskCondition(diskCondition.trim());
        }
        ex.setExchangeDate(Date.valueOf(exchangeDateStr));
        if (clientInfo != null && !clientInfo.trim().isEmpty()) {
            ex.setClientInfo(clientInfo.trim());
        }
        if (exchangeNumber != null && !exchangeNumber.trim().isEmpty()) {
            ex.setExchangeNumber(exchangeNumber.trim());
        }
        if (diskForExchange != null && !diskForExchange.trim().isEmpty()) {
            ex.setDiskForExchange(diskForExchange.trim());
        }
        if (newDisk != null && !newDisk.trim().isEmpty()) {
            ex.setNewDisk(newDisk.trim());
        }

        if (dao.createExchange(ex)) {
            response.sendRedirect("disk_exchange.jsp?success=created");
        } else {
            response.sendRedirect("disk_exchange.jsp?error=db_error");
        }
    }
}
