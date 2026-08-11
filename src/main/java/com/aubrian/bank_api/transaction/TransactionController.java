package com.aubrian.bank_api.transaction;

import java.util.Collection;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aubrian.bank_api.transaction.dto.CreateTransactionRequest;
import com.aubrian.bank_api.transaction.dto.TransactionResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/accounts/{accountId}/transactions")
public class TransactionController {
  
  private final TransactionService transactionService;

  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @PostMapping
  ResponseEntity<TransactionResponse> create(@PathVariable("accountId") UUID accountId, @Valid @RequestBody CreateTransactionRequest request) {
    return new ResponseEntity<>(transactionService.create(accountId, request), HttpStatus.CREATED);
  }

  @GetMapping
  Collection<TransactionResponse> getByAccountId(@PathVariable("accountId") UUID accountId) {
    return transactionService.getByAccountId(accountId);
  }
}
