package com.aubrian.bank_api.exception;

public class SameAccountTransferException extends RuntimeException {
  public SameAccountTransferException(String message) {
    super(message);
  }
}
