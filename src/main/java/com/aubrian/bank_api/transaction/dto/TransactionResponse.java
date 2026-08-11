package com.aubrian.bank_api.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.aubrian.bank_api.transaction.TransactionType;

public record TransactionResponse(
  UUID id,
  UUID accountId,
  TransactionType type,
  BigDecimal amount,
  LocalDateTime createdAt
) {
}
