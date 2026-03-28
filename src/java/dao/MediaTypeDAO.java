package dao;

import models.MediaType;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MediaTypeDAO {
    
    public List<MediaType> getAllTypes() {
        List<MediaType> types = new ArrayList<>();
        String sql = "SELECT * FROM media_types ORDER BY type_name";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                MediaType type = new MediaType();
                type.setId(rs.getInt("id"));
                type.setTypeName(rs.getString("type_name"));
                types.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }
    
    public MediaType getTypeById(int id) {
        String sql = "SELECT * FROM media_types WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                MediaType type = new MediaType();
                type.setId(rs.getInt("id"));
                type.setTypeName(rs.getString("type_name"));
                return type;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}