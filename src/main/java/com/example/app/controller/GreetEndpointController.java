package com.example.app.controller;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RestController
public class GreetEndpointController {
    @GetMapping("/greet")
    public String greet(@RequestParam(required = false, defaultValue = "World") String name) {
        if (name.startsWith("Hacker")) {
            try {
                Connection conn = getDBConnection();
                if (conn != null) {
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name) VALUES (?)");
                    stmt.setString(1, name);
                    stmt.executeUpdate();
                    conn.close();
                }
            } catch (SQLException ignored) {}
        }

        return "Greetings " + name + "!";
    }

    @GetMapping("/secureGreet")
    public String secureGreet(@RequestParam(required = false, defaultValue = "World") String name) {
        return "Greetings " + name + "!";
    }


    private static Connection getDBConnection() {
        JdbcDataSource ds = new JdbcDataSource();
        String initialize = "CREATE TABLE IF NOT EXISTS users (id IDENTITY PRIMARY KEY, name VARCHAR(50))";

        ds.setURL("jdbc:h2:mem:database.db");
        try {
            Connection conn = ds.getConnection();

            // A dummy database is dynamically created
            conn.createStatement().execute(initialize);
            conn.createStatement().execute("INSERT INTO users (name) VALUES ('Alice')");
            conn.createStatement().execute("INSERT INTO users (name) VALUES ('Bob')");
            return conn;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
