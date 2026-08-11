package com.aubrian.bank_api.transaction.dto;

import java.math.BigDecimal;

import com.aubrian.bank_api.transaction.TransactionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateTransactionRequest(
    @NotNull TransactionType type,
    @NotNull @Positive BigDecimal amount) {
}
