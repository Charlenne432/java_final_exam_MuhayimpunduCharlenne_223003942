package com.util;

import java.sql.*;

public class DB {
    public static Connection getConnection() throws SQLException {
     
        String url = "jdbc:mysql://localhost:3306/transport_analytic_system_db";
        String username = "root"; 
        String password = "";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
        
        return DriverManager.getConnection(url, username, password);
    }
    
  
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}