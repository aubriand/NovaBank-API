package com.aubrian.bank_api.transfer.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateTransferRequest(
  @NotNull UUID sourceAccountId,
  @NotNull UUID destinationAccountId,
  @NotNull @Positive BigDecimal amount
) {
}
