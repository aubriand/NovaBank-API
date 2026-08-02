package com.aubrian.bank_api.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aubrian.bank_api.security.dto.LoginRequest;
import com.aubrian.bank_api.security.service.JwtService;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Import(JwtIntegrationTests.ProtectedEnpointTestConfig.class)
public class JwtIntegrationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldValidateLogin() throws Exception {
    String username = "user@test.com";
    String password = "password";

    mockMvc.perform(
        post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(new LoginRequest(username, password))))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());
  }

  @Test
  void shouldReturnUnauthorizedWithBadPassword() throws Exception {
    String username = "user@test.com";
    String password = "false-password";

    mockMvc.perform(
        post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(new LoginRequest(username, password))))
        .andExpect(status().is(401));
  }

  @Test
  void shouldReturnUnauthorizedOnProtectedEndpointWithoutToken() throws Exception {
    mockMvc.perform(get("/test/protected"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldReturnAuthorizedOnProtectedEndpointWithValidToken() throws Exception {
    String token = jwtService.generateToken("user@test.com");

    mockMvc.perform(
        get("/test/protected")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(content().string("protected"));
  }

  @Test
  void shouldReturnUnauthorizedOnProtectedEndpointWithInvalidToken() throws Exception {
    String token = jwtService.generateToken("user@test.com");
    String invalidToken = token.substring(0, token.length() - 1)
        + (token.endsWith("X") ? "Y" : "X");

    mockMvc.perform(
        get("/test/protected")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldReturnOkOnAuthenticatedEndpointWithUserAccount() throws Exception {
    String token = jwtService.generateToken("user@test.com");

    mockMvc.perform(
        get("/test/any-role")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk());
  }

  @Test
  void shouldReturnForbiddenOnAdminEndpointWithUserAccount() throws Exception {
    String token = jwtService.generateToken("user@test.com");

    mockMvc.perform(
        get("/test/admin-role")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldReturnOkOnAdminEndpointWithAdminAccount() throws Exception {
    String token = jwtService.generateToken("admin@test.com");

    mockMvc.perform(
        get("/test/admin-role")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk());
  }

  private String asJsonString(Object object) throws Exception {
    return objectMapper.writeValueAsString(object);
  }

  @TestConfiguration
  static class ProtectedEnpointTestConfig {
    @RestController
    @RequestMapping("/test")
    static class ProtectedEnpointController {

      @GetMapping("/protected")
      String protectedEndpoint() {
        return "protected";
      }

      @GetMapping("/any-role")
      String anyRoleRequest() {
        return "any role";
      }

      @GetMapping("/admin-role")
      String adminRoleRequest() {
        return "admin role";
      }
    }
  }
}
