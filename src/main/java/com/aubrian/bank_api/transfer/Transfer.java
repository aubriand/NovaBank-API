package com.aubrian.bank_api.transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.aubrian.bank_api.account.Account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "transfers")
public class Transfer {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "source_account_id", nullable = false)
  private Account source;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "destination_account_id", nullable = false)
  private Account destination;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  void prePersist() {
    createdAt = LocalDateTime.now();
  }

  protected Transfer() {}

  public Transfer(Account source, Account destination, BigDecimal amount) {
    this.source = source;
    this.destination = destination;
    this.amount = amount;
  }

  public UUID getId() {
    return id;
  }

  public Account getSource() {
    return source;
  }

  public Account getDestination() {
    return destination;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
