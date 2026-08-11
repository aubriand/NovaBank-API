package com.aubrian.bank_api.transfer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import com.aubrian.bank_api.account.Account;
import com.aubrian.bank_api.account.AccountRepository;
import com.aubrian.bank_api.transfer.dto.CreateTransferRequest;

@SpringBootTest
@Sql(scripts = { "/test-cleanup.sql", "/test-users.sql", "/test-customers.sql",
    "/test-accounts.sql", "/test-transactions.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TransferServiceRollbackIntegrationTest {

  @MockitoBean
  private TransferRepository transferRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private TransferService transferService;

  private final UUID SOURCE_ACCOUNT_ID = UUID.fromString("732ec5a0-94e2-4c3d-8c19-2a4629120d10");
  private final UUID DEST_ACCOUNT_ID = UUID.fromString("732ec5a0-94e2-4c3d-8c19-2a4629120d11");
  
  @Test
  void shouldRollbackWhenTransferPersistenceFails() {
    Account sourceBefore = accountRepository.findById(SOURCE_ACCOUNT_ID).orElseThrow();
    Account destinationBefore = accountRepository.findById(DEST_ACCOUNT_ID).orElseThrow();

    BigDecimal sourceInitialBalance = sourceBefore.getBalance();
    BigDecimal destinationInitialBalance = destinationBefore.getBalance();

    when(transferRepository.save(any(Transfer.class)))
        .thenThrow(new RuntimeException("Persistence failure"));

    CreateTransferRequest request = new CreateTransferRequest(
        SOURCE_ACCOUNT_ID,
        DEST_ACCOUNT_ID,
        BigDecimal.valueOf(10));

    assertThrows(
        RuntimeException.class,
        () -> transferService.transfer(request));

    Account sourceAfter = accountRepository.findById(SOURCE_ACCOUNT_ID).orElseThrow();
    Account destinationAfter = accountRepository.findById(DEST_ACCOUNT_ID).orElseThrow();

    assertEquals(0, sourceInitialBalance.compareTo(sourceAfter.getBalance()));
    assertEquals(0, destinationInitialBalance.compareTo(destinationAfter.getBalance()));
  }

}
