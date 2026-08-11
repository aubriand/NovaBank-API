package com.aubrian.bank_api.transaction;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.aubrian.bank_api.security.service.JwtService;
import com.aubrian.bank_api.transaction.dto.CreateTransactionRequest;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = { "/test-cleanup.sql", "/test-users.sql", "/test-customers.sql",
    "/test-accounts.sql", "/test-transactions.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TransactionControllerIntegrationTests {
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
  void shouldValidDepositReturnCreatedAndBalanceAugmented() throws Exception {
    CreateTransactionRequest request = new CreateTransactionRequest(TransactionType.DEPOSIT, BigDecimal.valueOf(10.00));
    mockMvc.perform(
        post("/accounts/732ec5a0-94e2-4c3d-8c19-2a4629120d10/transactions")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.amount").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(10.00));

    mockMvc.perform(
        get("/accounts/732ec5a0-94e2-4c3d-8c19-2a4629120d10")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.balance").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(110.00));
  }

  @Test
  void shouldValidWithdrawalReturnCreatedAndBalanceDiminished() throws Exception {
    CreateTransactionRequest request = new CreateTransactionRequest(TransactionType.WITHDRAWAL,
        BigDecimal.valueOf(10.00));
    mockMvc.perform(
        post("/accounts/732ec5a0-94e2-4c3d-8c19-2a4629120d10/transactions")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.amount").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(10.00));

    mockMvc.perform(
        get("/accounts/732ec5a0-94e2-4c3d-8c19-2a4629120d10")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.balance").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(90.00));
  }

  @Test
  void shouldWithdrawalBiggerThanBalanceReturnConflictAndBalanceUnchanged() throws Exception {
    CreateTransactionRequest request = new CreateTransactionRequest(TransactionType.WITHDRAWAL,
        BigDecimal.valueOf(101.00));
    mockMvc.perform(
        post("/accounts/732ec5a0-94e2-4c3d-8c19-2a4629120d10/transactions")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isConflict())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
        .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Conflict"));

    mockMvc.perform(
        get("/accounts/732ec5a0-94e2-4c3d-8c19-2a4629120d10")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.balance").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(100.00));
  }

  @Test
  void shouldRejectedWithdrawalNotCreateTransaction() throws Exception {
    CreateTransactionRequest request = new CreateTransactionRequest(TransactionType.WITHDRAWAL,
        BigDecimal.valueOf(101.00));
    mockMvc.perform(
        post("/accounts/732ec5a0-94e2-4c3d-8c19-2a4629120d10/transactions")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isConflict())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
        .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Conflict"));

    mockMvc.perform(
        get("/accounts/732ec5a0-94e2-4c3d-8c19-2a4629120d10/transactions")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
  }

  @Test
  void shouldNullAmountReturnBadRequest() throws Exception {
    CreateTransactionRequest request = new CreateTransactionRequest(TransactionType.WITHDRAWAL, null);
    mockMvc.perform(
        post("/accounts/732ec5a0-94e2-4c3d-8c19-2a4629120d10/transactions")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
        .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Bad Request"));
  }

  @Test
  void shouldNegativeAmountReturnBadRequest() throws Exception {
    CreateTransactionRequest request = new CreateTransactionRequest(TransactionType.WITHDRAWAL,
        BigDecimal.valueOf(-1.00));
    mockMvc.perform(
        post("/accounts/732ec5a0-94e2-4c3d-8c19-2a4629120d10/transactions")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
        .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Bad Request"));
  }

  @Test
  void shouldUnknownAccountReturnNotFound() throws Exception {
    CreateTransactionRequest request = new CreateTransactionRequest(TransactionType.WITHDRAWAL,
        BigDecimal.valueOf(10.00));
    mockMvc.perform(
        post("/accounts/732ec5a0-94e2-4c3d-8c19-2a4629120d11/transactions")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
        .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Not Found"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Account not found"));
  }

  @Test
  void shouldGetTransactionsReturnOkWithTransactionsHistory() throws Exception {
    mockMvc.perform(
        get("/accounts/732ec5a0-94e2-4c3d-8c19-2a4629120d10/transactions")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].type").value("DEPOSIT"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount").value(100.00))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountId").value("732ec5a0-94e2-4c3d-8c19-2a4629120d10"));
  }
}
