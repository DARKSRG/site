package controllers;

import dao.UserDAO;
import models.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")  // ← ЭТО ВАЖНО!
public class RegisterServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        
        if (username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            
            response.sendRedirect("register.jsp?error=empty_fields");
            return;
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setFullName(fullName);
        
        UserDAO dao = new UserDAO();
        
        if (dao.userExists(username, email)) {
            response.sendRedirect("register.jsp?error=user_exists");
            return;
        }
        
        boolean success = dao.register(user);
        
        if (success) {
            response.sendRedirect("login.jsp?success=registered");
        } else {
            response.sendRedirect("register.jsp?error=db_error");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("register.jsp");
    }
}