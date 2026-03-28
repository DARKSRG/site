package dao;

import models.User;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    // Регистрация нового пользователя
    public boolean register(User user) {
        String sql = "INSERT INTO users (username, email, password, full_name) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword()); // уже должен быть хеширован!
            pstmt.setString(4, user.getFullName());
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Аутентификация пользователя
    public User authenticate(String login, String password) {
        String sql = "SELECT * FROM users WHERE username = ? OR email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, login);
            pstmt.setString(2, login);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Здесь нужно сравнить хеши паролей
                // Пока просто сравниваем строки (временно!)
                String dbPassword = rs.getString("password");
                if (dbPassword.equals(password)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setFullName(rs.getString("full_name"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Получить пользователя по ID
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setFullName(rs.getString("full_name"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                return user;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /** Список пользователей для фильтров (логин, ФИО) */
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, email, full_name FROM users ORDER BY username";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setFullName(rs.getString("full_name"));
                list.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Проверка существования пользователя
    public boolean userExists(String username, String email) {
        String sql = "SELECT * FROM users WHERE username = ? OR email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}