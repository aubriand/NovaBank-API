package com.aubrian.bank_api.transfer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aubrian.bank_api.account.Account;
import com.aubrian.bank_api.account.AccountRepository;
import com.aubrian.bank_api.exception.AccountNotFoundException;
import com.aubrian.bank_api.exception.SameAccountTransferException;
import com.aubrian.bank_api.transfer.dto.CreateTransferRequest;
import com.aubrian.bank_api.transfer.dto.TransferResponse;

@Service
public class TransferService {

  private final TransferRepository transferRepository;
  private final AccountRepository accountRepository;

  public TransferService(TransferRepository transferRepository, AccountRepository accountRepository) {
    this.transferRepository = transferRepository;
    this.accountRepository = accountRepository;
  }

  @Transactional
  public TransferResponse transfer(CreateTransferRequest request) {
    if (request.sourceAccountId().equals(request.destinationAccountId()))
      throw new SameAccountTransferException("Same account given for transfer");

    Account sourceAccount = accountRepository.findById(request.sourceAccountId())
        .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    Account destinationAccount = accountRepository.findById(request.destinationAccountId())
        .orElseThrow(() -> new AccountNotFoundException("Account not found"));

    sourceAccount.withdraw(request.amount());
    destinationAccount.deposit(request.amount());

    Transfer newTransfer = new Transfer(sourceAccount, destinationAccount, request.amount());
    Transfer transfer = transferRepository.save(newTransfer);
    return new TransferResponse(
        transfer.getId(),
        transfer.getAmount(),
        transfer.getCreatedAt(),
        transfer.getSource().getId(),
        transfer.getDestination().getId());
  }
}
