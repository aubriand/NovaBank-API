package com.aubrian.bank_api.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomerNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleCustomerNotFound(
      CustomerNotFoundException exception,
      HttpServletRequest request) {
    ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
        Instant.now(),
        HttpStatus.NOT_FOUND.value(),
        HttpStatus.NOT_FOUND.getReasonPhrase(),
        exception.getMessage(),
        request.getRequestURI());
    return new ResponseEntity<ApiErrorResponse>(apiErrorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidationException(
    MethodArgumentNotValidException exception,
    HttpServletRequest request
  ) {
    ApiErrorResponse response = new ApiErrorResponse(
      Instant.now(),
      HttpStatus.BAD_REQUEST.value(),
      HttpStatus.BAD_REQUEST.getReasonPhrase(),
      "Validation failed",
      request.getRequestURI()
    );

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleAccountNotFound(
      AccountNotFoundException exception,
      HttpServletRequest request) {
    ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
        Instant.now(),
        HttpStatus.NOT_FOUND.value(),
        HttpStatus.NOT_FOUND.getReasonPhrase(),
        exception.getMessage(),
        request.getRequestURI());
    return new ResponseEntity<ApiErrorResponse>(apiErrorResponse, HttpStatus.NOT_FOUND);
  }
}
