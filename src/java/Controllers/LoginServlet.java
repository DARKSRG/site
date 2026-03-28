package controllers;

import dao.UserDAO;
import models.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")  // ← ЭТО ВАЖНО!
public class LoginServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String login = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (login == null || login.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            
            response.sendRedirect("login.jsp?error=empty_fields");
            return;
        }
        
        UserDAO dao = new UserDAO();
        User user = dao.authenticate(login, password);
        
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect("profile.jsp");
        } else {
            response.sendRedirect("login.jsp?error=invalid");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}