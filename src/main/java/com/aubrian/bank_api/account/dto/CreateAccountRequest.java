package com.aubrian.bank_api.account.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CreateAccountRequest(
  @NotNull
  UUID customerId
) {
}
