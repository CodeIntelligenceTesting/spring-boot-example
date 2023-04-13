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

import com.code_intelligence.jazzer.api.FuzzerSecurityIssueMedium;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
@RestController
class GreeterApplication {

  private static Connection conn;

  public static class User implements Serializable {
    private static final long serialVersionUID = 123456789L;
    public String name;

    public User(String name) {
      name = name;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      name = (String) ois.readObject();
    }
  }

  public static User deserialize(ByteArrayInputStream stream) throws IOException {
    ObjectInputStream ois = new ObjectInputStream(stream);
    try {
      // Casting the result of readObject() occurs after the deserialization process ends
      // which make it possible to read any object and can lead to gadget chain attacks
      return (User) ois.readObject();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void connect()  {
    JdbcDataSource ds = new JdbcDataSource();
    String initialize = "CREATE TABLE IF NOT EXISTS users (id IDENTITY PRIMARY KEY, name VARCHAR(50))";

    ds.setURL("jdbc:h2:mem:database.db");
    try {
      conn = ds.getConnection();

      // A dummy database is dynamically created
      conn.createStatement().execute(initialize);
      conn.createStatement().execute("INSERT INTO users (name) VALUES ('Alice')");
      conn.createStatement().execute("INSERT INTO users (name) VALUES ('Bob')");


    } catch (SQLException e){
      e.printStackTrace();
    }
  }

  @GetMapping("/hello")
  public String insecureHello(@RequestParam(required = false, defaultValue = "World") String name)  {
    // We trigger an exception in the special case where the name is "attacker". This shows
    // how CI Fuzz can find this out and generates a test case triggering the exception
    // guarded by this check.
    // Black-box approaches lack insights into the code and thus cannot handle these cases.
    if (name.equalsIgnoreCase("attacker")) {
      // We throw an exception here to mimic the situation that something unexpected
      // occurred while handling the request.
      String className = name.substring(1);
      try {
        Class.forName(className);
      } catch (ClassNotFoundException e){
        e.printStackTrace();
      }
    }

    return "Hello " + name + "\n";
  }

  @GetMapping("/add")
  public String insecureAddUser(@RequestParam(required = false, defaultValue = "Eve") String name){
    // This service method inserts the username into a database
    connect();

    try {
      String query = String.format("INSERT INTO users (name) VALUES ('%s')", name);
      conn.createStatement().execute(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return "Added " + name;
  }

  public static void main(String[] args) {
    SpringApplication.run(GreeterApplication.class, args);
  }
}
