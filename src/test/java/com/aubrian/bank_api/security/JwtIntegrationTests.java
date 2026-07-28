package com.aubrian.bank_api.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.aubrian.bank_api.security.dto.LoginRequest;
import com.aubrian.bank_api.security.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtIntegrationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JwtService jwtService;

  @Test
  void shouldValidateLogin() throws Exception {
    String username = "admin";
    String password = "password";

    mockMvc.perform(
        post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(new LoginRequest(username, password))))
        .andExpect(status().is(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());
  }

  @Test
  void shouldReturnUnauthorizedWithBadPassword() throws Exception {
    String username = "admin";
    String password = "false-password";

    mockMvc.perform(
        post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(new LoginRequest(username, password))))
        .andExpect(status().is(401));
  }

  @Test
  void shouldReturnUnauthorizedOnProtectedEndpointWithoutToken() throws Exception {
    mockMvc.perform(
        post("/auth/protected-endpoint"))
        .andExpect(status().is(401));
  }

  @Test
  void shouldReturnAuthorizedOnProtectedEndpointWithToken() throws Exception {
    String username = "admin";

    String token = jwtService.generateToken(username);

    mockMvc.perform(
        get("/actuator/health")
            .headers(t -> 
              t.setBearerAuth(token)
            ))
        .andExpect(status().is(200));
  }

  @Test
  void shouldReturnUnauthorizedOnProtectedEndpointWithInvalidToken() throws Exception {
    String username = "admin";

    String token = jwtService.generateToken(username);
    String invalidToken = token.substring(0, token.length() - 1) + "X";

    mockMvc.perform(
        get("/actuator/health")
            .headers(t -> 
              t.setBearerAuth(invalidToken)
            ))
        .andExpect(status().is(401));
  }

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
