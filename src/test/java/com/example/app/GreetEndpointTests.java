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

package com.example.app;

import com.code_intelligence.jazzer.junit.FuzzTest;
import com.code_intelligence.jazzer.mutation.annotation.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest()
public class GreetEndpointTests {
  @Autowired private MockMvc mockMvc;

  @Test
  public void unitTestGreetDeveloper() throws Exception {
    mockMvc.perform(get("/greet").param("name", "Developer"));
  }

  @Test
  public void unitTestGreetContributor() throws Exception {
    mockMvc.perform(get("/greet").param("name", "Contributor"));
  }

  @FuzzTest
  public void fuzzTestGreet(@NotNull String name) throws Exception {
    mockMvc.perform(get("/greet").param("name", name));
  }

}
