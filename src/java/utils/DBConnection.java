package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/minisite"
            + "?useUnicode=true"
            + "&characterEncoding=UTF-8"
            + "&connectionCollation=utf8mb4_unicode_ci";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Драйвер MySQL загружен");
        } catch (ClassNotFoundException e) {
            System.out.println("Драйвер MySQL не найден!");
            e.printStackTrace();
        }
        
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}