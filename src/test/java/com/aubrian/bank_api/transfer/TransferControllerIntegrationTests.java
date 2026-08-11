package com.aubrian.bank_api.transfer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.MvcResult;

import com.aubrian.bank_api.security.service.JwtService;
import com.aubrian.bank_api.transfer.dto.CreateTransferRequest;
import com.aubrian.bank_api.transfer.dto.TransferResponse;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = { "/test-cleanup.sql", "/test-users.sql", "/test-customers.sql",
    "/test-accounts.sql", "/test-transactions.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TransferControllerIntegrationTests {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private TransferRepository transferRepository;

  String token;

  private final UUID SOURCE_ACCOUNT_ID = UUID.fromString("732ec5a0-94e2-4c3d-8c19-2a4629120d10");
  private final UUID DEST_ACCOUNT_ID = UUID.fromString("732ec5a0-94e2-4c3d-8c19-2a4629120d11");

  @BeforeEach
  void generateToken() throws Exception {
    this.token = jwtService.generateToken("user@test.com");
  }

  private String asJsonString(Object object) throws Exception {
    return objectMapper.writeValueAsString(object);
  }

  @Test
  void shouldCreationOfValidTransferReturnCreated() throws Exception {
    CreateTransferRequest request = new CreateTransferRequest(
        SOURCE_ACCOUNT_ID,
        DEST_ACCOUNT_ID,
        BigDecimal.valueOf(10.00));
    MvcResult result = mockMvc.perform(
        post("/transfers")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.sourceAccountId").value(SOURCE_ACCOUNT_ID.toString()))
        .andExpect(jsonPath("$.destinationAccountId").value(DEST_ACCOUNT_ID.toString()))
        .andReturn();
    String responseBody = result.getResponse().getContentAsString();
    TransferResponse response = objectMapper.readValue(responseBody, TransferResponse.class);
    assertTrue(transferRepository.existsById(response.id()));
  }

  @Test
  void shouldValidTransferWithdrawSourceAccount() throws Exception {
    CreateTransferRequest request = new CreateTransferRequest(
        SOURCE_ACCOUNT_ID,
        DEST_ACCOUNT_ID,
        BigDecimal.valueOf(10.00));
    mockMvc.perform(
        post("/transfers")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isCreated());

    mockMvc.perform(
        get("/accounts/" + SOURCE_ACCOUNT_ID)
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance").value(90.00));
  }

  @Test
  void shouldValidTransferDepositDestinationAccount() throws Exception {
    CreateTransferRequest request = new CreateTransferRequest(
        SOURCE_ACCOUNT_ID,
        DEST_ACCOUNT_ID,
        BigDecimal.valueOf(10.00));
    mockMvc.perform(
        post("/transfers")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isCreated());

    mockMvc.perform(
        get("/accounts/" + DEST_ACCOUNT_ID)
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance").value(110.00));
  }

  @Test
  void shouldUnknownSourceAccountReturnNotFound() throws Exception {
    CreateTransferRequest request = new CreateTransferRequest(
        UUID.fromString("00000000-0000-0000-0000-000000000000"), DEST_ACCOUNT_ID, BigDecimal.valueOf(10));

    mockMvc.perform(
        post("/transfers")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Account not found"));
  }

  @Test
  void shouldUnknownDestinationAccountReturnNotFound() throws Exception {
    CreateTransferRequest request = new CreateTransferRequest(
        SOURCE_ACCOUNT_ID, UUID.fromString("00000000-0000-0000-0000-000000000000"), BigDecimal.valueOf(10));

    mockMvc.perform(
        post("/transfers")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Account not found"));
  }

  @Test
  void shouldNegativeAmountReturnBadRequest() throws Exception {
    CreateTransferRequest request = new CreateTransferRequest(
        SOURCE_ACCOUNT_ID, DEST_ACCOUNT_ID, BigDecimal.valueOf(-10));

    mockMvc.perform(
        post("/transfers")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"));
  }

  @Test
  void shouldNullAmountReturnBadRequest() throws Exception {
    CreateTransferRequest request = new CreateTransferRequest(
        SOURCE_ACCOUNT_ID, DEST_ACCOUNT_ID, null);

    mockMvc.perform(
        post("/transfers")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"));
  }

  @Test
  void shouldSameAccountReturnConflict() throws Exception {
    CreateTransferRequest request = new CreateTransferRequest(
        SOURCE_ACCOUNT_ID, SOURCE_ACCOUNT_ID, BigDecimal.valueOf(10));

    mockMvc.perform(
        post("/transfers")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("Conflict"));
  }

  @Test
  void shouldInsufficentBalanceReturnConflict() throws Exception {
    CreateTransferRequest request = new CreateTransferRequest(
        SOURCE_ACCOUNT_ID, DEST_ACCOUNT_ID, BigDecimal.valueOf(110));

    mockMvc.perform(
        post("/transfers")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("Conflict"));
  }
}
