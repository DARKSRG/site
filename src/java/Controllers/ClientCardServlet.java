package controllers;

import dao.ClientCardDAO;
import models.ClientCard;
import models.User;
import java.io.IOException;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ClientCardServlet")
public class ClientCardServlet extends HttpServlet {
    
    private ClientCardDAO cardDAO = new ClientCardDAO();
    
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
            int cardId = Integer.parseInt(request.getParameter("id"));
            cardDAO.deleteCard(cardId, user.getId());
            response.sendRedirect("client_cards.jsp");
        } else {
            response.sendRedirect("client_cards.jsp");
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
        
        // Получаем параметры из формы (теперь с правильной кодировкой)
        String lastName = request.getParameter("lastName");
        String firstName = request.getParameter("firstName");
        String middleName = request.getParameter("middleName");
        String registrationDate = request.getParameter("registrationDate");
        
        // Валидация
        if (lastName == null || lastName.trim().isEmpty() ||
            firstName == null || firstName.trim().isEmpty() ||
            registrationDate == null || registrationDate.trim().isEmpty()) {
            
            response.sendRedirect("client_cards.jsp?error=empty_fields");
            return;
        }
        
        // Создаем карточку
        ClientCard card = new ClientCard();
        card.setUserId(user.getId());
        card.setLastName(lastName);
        card.setFirstName(firstName);
        card.setMiddleName(middleName);
        card.setRegistrationDate(Date.valueOf(registrationDate));
        
        // Сохраняем
        boolean success = cardDAO.createCard(card);
        
        if (success) {
            response.sendRedirect("client_cards.jsp?success=created");
        } else {
            response.sendRedirect("client_cards.jsp?error=db_error");
        }
    }
}