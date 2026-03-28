package dao;

import models.MediaReceipt;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MediaReceiptDAO {
    
    public boolean createReceipt(MediaReceipt receipt) {
        String sql = "INSERT INTO media_receipts (user_id, media_type_id, title, release_year, "
                   + "quantity, receipt_date, document_number, supplier_id, supplier_info, "
                   + "batch_number, total_amount, notes) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, receipt.getUserId());
            pstmt.setInt(2, receipt.getMediaTypeId());
            pstmt.setString(3, receipt.getTitle());
            pstmt.setObject(4, receipt.getReleaseYear());
            pstmt.setInt(5, receipt.getQuantity());
            pstmt.setDate(6, receipt.getReceiptDate());
            pstmt.setString(7, receipt.getDocumentNumber());
            pstmt.setObject(8, receipt.getSupplierId());
            pstmt.setString(9, receipt.getSupplierInfo());
            pstmt.setString(10, receipt.getBatchNumber());
            pstmt.setObject(11, receipt.getTotalAmount());
            pstmt.setString(12, receipt.getNotes());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<MediaReceipt> getReceiptsByUserId(int userId) {
        List<MediaReceipt> receipts = new ArrayList<>();
        String sql = "SELECT r.*, mt.type_name as media_type_name, s.supplier_name "
                   + "FROM media_receipts r "
                   + "LEFT JOIN media_types mt ON r.media_type_id = mt.id "
                   + "LEFT JOIN suppliers s ON r.supplier_id = s.id "
                   + "WHERE r.user_id = ? "
                   + "ORDER BY r.receipt_date DESC, r.created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                MediaReceipt receipt = new MediaReceipt();
                receipt.setId(rs.getInt("id"));
                receipt.setUserId(rs.getInt("user_id"));
                receipt.setMediaTypeId(rs.getInt("media_type_id"));
                receipt.setMediaTypeName(rs.getString("media_type_name"));
                receipt.setTitle(rs.getString("title"));
                receipt.setReleaseYear(rs.getInt("release_year"));
                receipt.setQuantity(rs.getInt("quantity"));
                receipt.setReceiptDate(rs.getDate("receipt_date"));
                receipt.setDocumentNumber(rs.getString("document_number"));
                receipt.setSupplierId(rs.getInt("supplier_id"));
                receipt.setSupplierName(rs.getString("supplier_name"));
                receipt.setSupplierInfo(rs.getString("supplier_info"));
                receipt.setBatchNumber(rs.getString("batch_number"));
                receipt.setTotalAmount(rs.getDouble("total_amount"));
                receipt.setNotes(rs.getString("notes"));
                receipt.setCreatedAt(rs.getDate("created_at"));
                receipts.add(receipt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receipts;
    }

    /**
     * Приходы пользователя с положительным остатком (для оформления продажи).
     */
    public List<MediaReceipt> getReceiptsWithAvailableStock(int userId) {
        List<MediaReceipt> receipts = new ArrayList<>();
        String sql = "SELECT r.*, mt.type_name AS media_type_name, s.supplier_name, "
                + "(r.quantity - COALESCE((SELECT SUM(ms.quantity_sold) FROM media_sales ms "
                + "WHERE ms.media_receipt_id = r.id), 0)) AS stock_left "
                + "FROM media_receipts r "
                + "LEFT JOIN media_types mt ON r.media_type_id = mt.id "
                + "LEFT JOIN suppliers s ON r.supplier_id = s.id "
                + "WHERE r.user_id = ? AND (r.quantity - COALESCE((SELECT SUM(ms2.quantity_sold) "
                + "FROM media_sales ms2 WHERE ms2.media_receipt_id = r.id), 0)) > 0 "
                + "ORDER BY r.receipt_date DESC, r.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MediaReceipt receipt = new MediaReceipt();
                receipt.setId(rs.getInt("id"));
                receipt.setUserId(rs.getInt("user_id"));
                receipt.setMediaTypeId(rs.getInt("media_type_id"));
                receipt.setMediaTypeName(rs.getString("media_type_name"));
                receipt.setTitle(rs.getString("title"));
                receipt.setReleaseYear(rs.getInt("release_year"));
                if (rs.wasNull()) {
                    receipt.setReleaseYear(null);
                }
                receipt.setQuantity(rs.getInt("quantity"));
                receipt.setReceiptDate(rs.getDate("receipt_date"));
                receipt.setDocumentNumber(rs.getString("document_number"));
                receipt.setSupplierId(rs.getInt("supplier_id"));
                if (rs.wasNull()) {
                    receipt.setSupplierId(null);
                }
                receipt.setSupplierName(rs.getString("supplier_name"));
                receipt.setSupplierInfo(rs.getString("supplier_info"));
                receipt.setBatchNumber(rs.getString("batch_number"));
                receipt.setTotalAmount(rs.getDouble("total_amount"));
                if (rs.wasNull()) {
                    receipt.setTotalAmount(null);
                }
                receipt.setNotes(rs.getString("notes"));
                receipt.setCreatedAt(rs.getDate("created_at"));
                receipt.setAvailableStock(rs.getInt("stock_left"));
                receipts.add(receipt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receipts;
    }

    public int getAvailableStock(int receiptId, int userId) {
        String sql = "SELECT (r.quantity - COALESCE((SELECT SUM(ms.quantity_sold) FROM media_sales ms "
                + "WHERE ms.media_receipt_id = r.id), 0)) AS stock_left "
                + "FROM media_receipts r WHERE r.id = ? AND r.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, receiptId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("stock_left");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public boolean deleteReceipt(int id, int userId) {
        String sql = "DELETE FROM media_receipts WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}