package dao;

import models.ClientCard;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientCardDAO {
    
    // Генерация номера карточки (формат: КЛ-ГГГГММДД-XXXX)
    private String generateCardNumber() {
        java.util.Date now = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(now);
        
        // Получаем последний ID для формирования номера
        String sql = "SELECT COUNT(*) FROM client_cards";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return String.format("КЛ-%s-%04d", datePart, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "КЛ-" + datePart + "-0001";
    }
    
    // Создание новой карточки
    public boolean createCard(ClientCard card) {
        String sql = "INSERT INTO client_cards (user_id, last_name, first_name, middle_name, registration_date, card_number) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Если номер карточки не задан, генерируем автоматически
            if (card.getCardNumber() == null || card.getCardNumber().isEmpty()) {
                card.setCardNumber(generateCardNumber());
            }
            
            pstmt.setInt(1, card.getUserId());
            pstmt.setString(2, card.getLastName());
            pstmt.setString(3, card.getFirstName());
            pstmt.setString(4, card.getMiddleName());
            pstmt.setDate(5, card.getRegistrationDate());
            pstmt.setString(6, card.getCardNumber());
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Получить все карточки пользователя
    public List<ClientCard> getCardsByUserId(int userId) {
        List<ClientCard> cards = new ArrayList<>();
        String sql = "SELECT * FROM client_cards WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ClientCard card = new ClientCard();
                card.setId(rs.getInt("id"));
                card.setUserId(rs.getInt("user_id"));
                card.setLastName(rs.getString("last_name"));
                card.setFirstName(rs.getString("first_name"));
                card.setMiddleName(rs.getString("middle_name"));
                card.setRegistrationDate(rs.getDate("registration_date"));
                card.setCardNumber(rs.getString("card_number"));
                card.setCreatedAt(rs.getDate("created_at"));
                cards.add(card);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }
    
    // Получить карточку по ID
    public ClientCard getCardById(int id) {
        String sql = "SELECT * FROM client_cards WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                ClientCard card = new ClientCard();
                card.setId(rs.getInt("id"));
                card.setUserId(rs.getInt("user_id"));
                card.setLastName(rs.getString("last_name"));
                card.setFirstName(rs.getString("first_name"));
                card.setMiddleName(rs.getString("middle_name"));
                card.setRegistrationDate(rs.getDate("registration_date"));
                card.setCardNumber(rs.getString("card_number"));
                card.setCreatedAt(rs.getDate("created_at"));
                return card;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Удалить карточку
    public boolean deleteCard(int id, int userId) {
        String sql = "DELETE FROM client_cards WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.setInt(2, userId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}