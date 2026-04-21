package com.blog.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // CHANGE THESE to match your MySQL setup
    private static final String URL = "jdbc:mysql://localhost:3306/advanced_blog";
    private static final String USER = "root";
    private static final String PASSWORD = "bankai";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load the MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish Connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found! Check your pom.xml.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection failed! Is MySQL running?");
            e.printStackTrace();
        }
        return connection;
    }
}