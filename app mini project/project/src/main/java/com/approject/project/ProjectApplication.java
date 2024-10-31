package com.approject.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class ProjectApplication {

    // MySQL Database URL, username, and password
    private static final String DB_URL = "localhost";
    private static final String DB_USER = "root"; // Set your MySQL username here
    private static final String DB_PASSWORD = "root"; // Set your MySQL password here

    // Main method to run the application
    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }

    // POST endpoint to sign up a new user
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM user WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, user.getUsername());
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
                }
            }

            String sql = "INSERT INTO user (username, password) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPassword());
                stmt.executeUpdate();
            }

            return new ResponseEntity<>("User signed up successfully", HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error signing up user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST endpoint to login a user
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM user WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, user.getUsername());
                ResultSet rs = stmt.executeQuery();
                
                if (!rs.next()) {
                    return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
                }

                String storedPassword = rs.getString("password");
                if (storedPassword.equals(user.getPassword())) {
                    return new ResponseEntity<>("Login successful", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Invalid password", HttpStatus.UNAUTHORIZED);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error during login", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // User model
    static class User {
        private String username;
        private String password;

        public User() {
        }

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
