package com.aubrian.bank_api.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
  @NotBlank
  @Email
  String email,

  @NotBlank
  String firstName,

  @NotBlank
  String lastName,

  @NotBlank
  String address,

  @NotBlank
  String country,

  @NotBlank
  @Size(max = 20)
  String postalCode
) {
}
