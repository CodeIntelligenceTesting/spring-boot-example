package com.example;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest()
public class GreeterApplicationTests {
  @Autowired private MockMvc mockMvc;

  @FuzzTest
  public void fuzzTestHello(FuzzedDataProvider data) throws Exception {
    String name = data.consumeRemainingAsString();
    mockMvc.perform(get("/hello").param("name", name));
  }
}
