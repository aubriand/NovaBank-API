package com.aubrian.bank_api.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigIntegrationTests {
  
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  void shouldAllowAnonymousAccessToHealthEndpoint() throws Exception {
    mockMvc.perform(get("/actuator/health"))
      .andExpect(status().isOk());
  }

  @Test
  void shouldRejectAnonymousAccessToProtectedEndpoints() throws Exception {
    mockMvc.perform(get("/api/non-existing-endpoint"))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldExposeWorkingPasswordEncoder() {
    String rawPassword = "StrongPassword123!";
    String encodedPassword = passwordEncoder.encode(rawPassword);

    assertNotEquals(rawPassword, encodedPassword);
    assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
  }
}
