package dao;

import models.MediaSale;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MediaSaleDAO {

    public List<MediaSale> getSalesByUserId(int userId) {
        List<MediaSale> list = new ArrayList<>();
        String sql = "SELECT s.*, r.title AS receipt_title, mt.type_name AS media_type_name "
                + "FROM media_sales s "
                + "INNER JOIN media_receipts r ON s.media_receipt_id = r.id "
                + "INNER JOIN media_types mt ON r.media_type_id = mt.id "
                + "WHERE s.user_id = ? "
                + "ORDER BY s.sale_date DESC, s.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MediaSale sale = mapRow(rs);
                list.add(sale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean createSale(MediaSale sale) {
        String sql = "INSERT INTO media_sales (user_id, media_receipt_id, sale_date, quantity_sold, "
                + "sale_amount, unit_price, customer_name, customer_phone, notes) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sale.getUserId());
            pstmt.setInt(2, sale.getMediaReceiptId());
            pstmt.setDate(3, sale.getSaleDate());
            pstmt.setInt(4, sale.getQuantitySold());
            pstmt.setDouble(5, sale.getSaleAmount());
            pstmt.setObject(6, sale.getUnitPrice());
            pstmt.setString(7, sale.getCustomerName());
            pstmt.setString(8, sale.getCustomerPhone());
            pstmt.setString(9, sale.getNotes());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSale(int saleId, int userId) {
        String sql = "DELETE FROM media_sales WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, saleId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static MediaSale mapRow(ResultSet rs) throws SQLException {
        MediaSale sale = new MediaSale();
        sale.setId(rs.getInt("id"));
        sale.setUserId(rs.getInt("user_id"));
        sale.setMediaReceiptId(rs.getInt("media_receipt_id"));
        sale.setReceiptTitle(rs.getString("receipt_title"));
        sale.setMediaTypeName(rs.getString("media_type_name"));
        sale.setSaleDate(rs.getDate("sale_date"));
        sale.setQuantitySold(rs.getInt("quantity_sold"));
        sale.setSaleAmount(rs.getDouble("sale_amount"));
        double up = rs.getDouble("unit_price");
        if (!rs.wasNull()) {
            sale.setUnitPrice(up);
        }
        sale.setCustomerName(rs.getString("customer_name"));
        sale.setCustomerPhone(rs.getString("customer_phone"));
        sale.setNotes(rs.getString("notes"));
        sale.setCreatedAt(rs.getTimestamp("created_at"));
        return sale;
    }
}
