package com.aubrian.bank_api.customer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.aubrian.bank_api.customer.dto.CreateCustomerRequest;
import com.aubrian.bank_api.security.service.JwtService;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/test-customers.sql", "/test-users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CustomerIntegrationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JwtService jwtService;

  private String token;

  @BeforeEach
  void generateToken() throws Exception {
    this.token = jwtService.generateToken("user@test.com");
  }

  @Test
  void shouldCreateCustomerReturnCreated() throws Exception {
    CreateCustomerRequest request = new CreateCustomerRequest(
        "test@test.fr",
        "Test",
        "Test",
        "test street",
        "France",
        "75000");
    mockMvc.perform(
        post("/customers")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@test.fr"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Test"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.country").value("France"));
  }

  @Test
  void shouldCreateCustomerReturnBadRequest() throws Exception {
    CreateCustomerRequest request = new CreateCustomerRequest(
        "testtestfr",
        "Test",
        "Test",
        "test street",
        "France",
        "75000");
    mockMvc.perform(
        post("/customers")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(asJsonString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
        .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Bad Request"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"));
  }

  @Test
  void shouldGetCustomerByIdReturnOk() throws Exception {
    mockMvc.perform(
        get("/customers/732ec5a0-94e2-4c3d-8c19-2a4629120d07")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("customer@test.com"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Customer"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.postalCode").value("75000"));
  }

  @Test
  void shouldGetCustomerByIdReturnNotFound() throws Exception {
    mockMvc.perform(
        get("/customers/00000000-0000-0000-0000-000000000000")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
        .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Not Found"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Customer not found"));
  }

  private String asJsonString(Object object) throws Exception {
    return objectMapper.writeValueAsString(object);
  }
}
