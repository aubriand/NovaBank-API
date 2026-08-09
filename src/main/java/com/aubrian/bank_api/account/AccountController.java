package com.aubrian.bank_api.account;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aubrian.bank_api.account.dto.AccountResponse;
import com.aubrian.bank_api.account.dto.CreateAccountRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/accounts")
public class AccountController {

  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }
  
  @PostMapping
  ResponseEntity<AccountResponse> create(@Valid @RequestBody CreateAccountRequest request) {
    return new ResponseEntity<AccountResponse>(accountService.create(request), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  AccountResponse getById(@PathVariable("id") UUID id) {
    return accountService.getById(id);
  }
}
