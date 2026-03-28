package dao;

import models.Supplier;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    
    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY supplier_name";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setId(rs.getInt("id"));
                supplier.setSupplierName(rs.getString("supplier_name"));
                supplier.setContactPerson(rs.getString("contact_person"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setEmail(rs.getString("email"));
                supplier.setAddress(rs.getString("address"));
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }
    
    public boolean addSupplier(Supplier supplier) {
        String sql = "INSERT INTO suppliers (supplier_name, contact_person, phone, email, address) "
                   + "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, supplier.getSupplierName());
            pstmt.setString(2, supplier.getContactPerson());
            pstmt.setString(3, supplier.getPhone());
            pstmt.setString(4, supplier.getEmail());
            pstmt.setString(5, supplier.getAddress());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}