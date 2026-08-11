package com.aubrian.bank_api.transaction;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aubrian.bank_api.account.Account;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
  List<Transaction> findByAccountOrderByCreatedAtDesc(Account account);
}
