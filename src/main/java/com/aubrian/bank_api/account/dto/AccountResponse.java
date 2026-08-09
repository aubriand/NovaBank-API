package com.aubrian.bank_api.account.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
  UUID id,
  UUID customerId,
  String iban,
  BigDecimal balance
) {
}
