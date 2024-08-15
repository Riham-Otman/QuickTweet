package com.project.backend.controller.login;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testAuthentication() throws Exception {
    String loginRequestBody = "{\"username\":\"root\",\"password\":\"dsadsa@A1\"}";
    String contentType = HttpHeaders.CONTENT_TYPE;
    String jsonType = "application/json";

    this.mockMvc.perform(post("/login").content(loginRequestBody).header(contentType, jsonType))
        .andDo(print()).andExpect(status().isOk());

  }

  @Test
  public void testAuthenticationFailure() throws Exception {
      String loginRequestBody = "{\"username\":\"root\",\"password\":\"wrongPassword\"}";
      String contentType = HttpHeaders.CONTENT_TYPE;
      String jsonType = "application/json";

      this.mockMvc.perform(post("/login").content(loginRequestBody).header(contentType, jsonType))
          .andDo(print()).andExpect(status().isUnauthorized());
  }
}
