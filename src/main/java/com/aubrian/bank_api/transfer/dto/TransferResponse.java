package com.aubrian.bank_api.transfer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferResponse(
  UUID id,
  BigDecimal amount,
  LocalDateTime createdAt,
  UUID sourceAccountId,
  UUID destinationAccountId
) {
}
