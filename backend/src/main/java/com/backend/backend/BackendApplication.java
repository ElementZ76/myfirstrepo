package com.backend.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) // Exclude DataSource auto-configuration
@RestController
public class BackendApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        initializeDatabase();
    }

    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3333/project"; // Update as per your MySQL setup
    private static final String USERNAME = "root"; // Change as necessary
    private static final String PASSWORD = "test"; // Change as necessary

    // Create users table on application startup
    private static void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS users (username VARCHAR(50) PRIMARY KEY, password VARCHAR(255) NOT NULL)")) {
            statement.executeUpdate();
            System.out.println("Database connected and table created successfully!");
        } catch (Exception e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }

    // Signup endpoint
    @GetMapping("/signup")
    public String signup(@RequestParam String username, @RequestParam String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
            return "Signup successful!";
        } catch (Exception e) {
            return "Signup failed: User may already exist.";
        }
    }

    // Login endpoint
    @GetMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return (count > 0) ? "Login successful!" : "Invalid credentials!";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error during login: " + e.getMessage();
        }
        return "Invalid credentials!";
    }

    // Global CORS configuration to allow requests from localhost:8000
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:8000");
    }
}

