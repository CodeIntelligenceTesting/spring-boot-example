/*
 * Copyright 2023 Code Intelligence GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
@RestController
class GreeterApplication {
  @GetMapping("/hello")
  public String insecureHello(@RequestParam(required = false, defaultValue = "World") String name) {
    if (name.startsWith("execute:")) {
      // SECURITY ALERT!
      // This leads to a Remote Code Execution vulnerability
      // by loading a class that an attacker control.
      String className = name.substring(8);
      try {
        Class.forName(className);
      } catch (ClassNotFoundException ignored){}
    }
    return "Hello " + name + "!";
  }

  @GetMapping("/greet")
  public String insecureAddUser(@RequestParam(required = false, defaultValue = "World") String name){
    if (name.startsWith("Hacker")) {
      try {
        Connection conn = getDBConnection();
        if (conn != null) {
          String query = String.format("INSERT INTO users (name) VALUES ('%s')", name);
          conn.createStatement().execute(query);
          conn.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    return "Greetings " + name + "!";
  }


  private static Connection getDBConnection()  {
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

    } catch (SQLException e){
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {
    SpringApplication.run(GreeterApplication.class, args);
  }
}
