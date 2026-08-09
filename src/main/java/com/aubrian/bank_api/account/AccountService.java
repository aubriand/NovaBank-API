package com.aubrian.bank_api.account;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aubrian.bank_api.account.dto.AccountResponse;
import com.aubrian.bank_api.account.dto.CreateAccountRequest;
import com.aubrian.bank_api.customer.Customer;
import com.aubrian.bank_api.customer.CustomerRepository;
import com.aubrian.bank_api.exception.AccountNotFoundException;
import com.aubrian.bank_api.exception.CustomerNotFoundException;


@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final CustomerRepository customerRepository;

  public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
    this.accountRepository = accountRepository;
    this.customerRepository = customerRepository;
  }

  @Transactional
  public AccountResponse create(CreateAccountRequest request) {
    Customer customer = customerRepository.findById(request.customerId())
        .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
    Account newAccount = new Account(
        customer,
        "NB" + UUID.randomUUID().toString().replace("-", ""),
        BigDecimal.ZERO);
    Account account = accountRepository.save(newAccount);
    return getResponse(account);
  }

  @Transactional(readOnly = true)
  public AccountResponse getById(UUID id) {
    Account account = accountRepository.findById(id)
        .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    return getResponse(account);
  }

  private AccountResponse getResponse(Account account) {
    AccountResponse response = new AccountResponse(
        account.getId(),
        account.getCustomer().getId(),
        account.getIban(),
        account.getBalance());
    return response;
  }
}
