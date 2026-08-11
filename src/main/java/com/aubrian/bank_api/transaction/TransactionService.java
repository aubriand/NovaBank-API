package com.aubrian.bank_api.transaction;

import java.util.Collection;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aubrian.bank_api.account.Account;
import com.aubrian.bank_api.account.AccountRepository;
import com.aubrian.bank_api.exception.AccountNotFoundException;
import com.aubrian.bank_api.transaction.dto.CreateTransactionRequest;
import com.aubrian.bank_api.transaction.dto.TransactionResponse;


@Service
public class TransactionService {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;

  public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
    this.accountRepository = accountRepository;
    this.transactionRepository = transactionRepository;
  }

  @Transactional
  public TransactionResponse create(UUID accountId, CreateTransactionRequest request) {
    Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    switch (request.type()) {
      case DEPOSIT:
        account.deposit(request.amount());
        break;

      case WITHDRAWAL:
        account.withdraw(request.amount());
        break;

      default:
        break;
    }
    accountRepository.save(account);
    Transaction newTransaction = transactionRepository.save(new Transaction(account, request.type(), request.amount()));
    TransactionResponse response = getResponse(newTransaction);
    return response;
  }

  @Transactional(readOnly = true)
  public Collection<TransactionResponse> getByAccountId(UUID accountId) {
    Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    Collection<Transaction> transactions = transactionRepository.findByAccountOrderByCreatedAtDesc(account);
    Collection<TransactionResponse> response = transactions.stream().map(t -> getResponse(t)).toList();
    return response;
  }

  private TransactionResponse getResponse(Transaction newTransaction) {
    TransactionResponse response = new TransactionResponse(
        newTransaction.getId(),
        newTransaction.getAccount().getId(),
        newTransaction.getType(),
        newTransaction.getAmount(),
        newTransaction.getCreatedAt());
    return response;
  }
}
