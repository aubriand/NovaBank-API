package com.aubrian.bank_api.customer;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aubrian.bank_api.customer.dto.CreateCustomerRequest;
import com.aubrian.bank_api.customer.dto.CustomerResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customers")
public class CustomerController {
  
  private final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping
  public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
    return new ResponseEntity<CustomerResponse>(customerService.create(request), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public CustomerResponse getById(@PathVariable("id") UUID id) {
    return customerService.getById(id);
  }
}
