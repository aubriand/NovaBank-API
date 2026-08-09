package com.aubrian.bank_api.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.startsWith;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.aubrian.bank_api.account.dto.CreateAccountRequest;
import com.aubrian.bank_api.security.service.JwtService;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = { "/test-users.sql", "/test-customers.sql",
    "/test-accounts.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AccountControllerIntegrationTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JwtService jwtService;

  String token;

  @BeforeEach
  void generateToken() throws Exception {
    this.token = jwtService.generateToken("user@test.com");
  }

  private String asJsonString(Object object) throws Exception {
    return objectMapper.writeValueAsString(object);
  }

  @Test
  void shouldCreateAccountWithExistingCustomerReturnCreated() throws Exception {
    CreateAccountRequest request = new CreateAccountRequest(UUID.fromString("732ec5a0-94e2-4c3d-8c19-2a4629120d07"));
    mockMvc.perform(
        post("/accounts")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value("732ec5a0-94e2-4c3d-8c19-2a4629120d07"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.iban", startsWith("NB")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(BigDecimal.ZERO))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
  }

  @Test
  void shouldCreateAccountWithUnknownCustomerReturnNotFound() throws Exception {
    CreateAccountRequest request = new CreateAccountRequest(UUID.fromString("732ec5a0-94e2-4c3d-8c19-2a4629120d08"));
    mockMvc.perform(
        post("/accounts")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
        .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Not Found"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Customer not found"));
  }

  @Test
  void shouldCreateAccountWithNullCustomerReturnBadRequest() throws Exception {
    CreateAccountRequest request = new CreateAccountRequest(null);
    mockMvc.perform(
        post("/accounts")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
        .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Bad Request"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"));
  }

  @Test
  void shouldGetExistingAccountReturnOk() throws Exception {
    mockMvc.perform(
        get("/accounts/732ec5a0-94e2-4c3d-8c19-2a4629120d10")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value("732ec5a0-94e2-4c3d-8c19-2a4629120d07"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.iban", startsWith("NB")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(BigDecimal.valueOf(250.0)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
  }

  @Test
  void shouldGetUnknownAccountReturnNotFound() throws Exception {
    mockMvc.perform(
        get("/accounts/732ec5a0-94e2-4c3d-8c19-2a4629120d11")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
        .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Not Found"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Account not found"));
  }
}
