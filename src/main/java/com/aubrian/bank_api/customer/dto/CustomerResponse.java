package com.aubrian.bank_api.customer.dto;

import java.util.UUID;

public record CustomerResponse(
  UUID id,
  String email,
  String firstName,
  String lastName,
  String address,
  String country,
  String postalCode
) {
}
