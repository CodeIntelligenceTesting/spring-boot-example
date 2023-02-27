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

@SpringBootApplication
@RestController
class GreeterApplication {
  @GetMapping("/hello")
  public String insecureHello(@RequestParam(required = false, defaultValue = "World") String name)
      throws Exception {
    if (name.equals("Attacker")) {
      throw new Exception("We panic when trying to greet an attacker!");
    }
    return "Hello " + name;
  }

  public static void main(String[] args) {
    SpringApplication.run(GreeterApplication.class, args);
  }
}
