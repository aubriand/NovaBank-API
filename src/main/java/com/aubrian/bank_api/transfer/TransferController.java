package com.aubrian.bank_api.transfer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aubrian.bank_api.transfer.dto.CreateTransferRequest;
import com.aubrian.bank_api.transfer.dto.TransferResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transfers")
public class TransferController {
  
  private final TransferService transferService;

  public TransferController(TransferService transferService) {
    this.transferService = transferService;
  }

  @PostMapping
  public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody CreateTransferRequest request) {
    return new ResponseEntity<>(transferService.transfer(request), HttpStatus.CREATED);
  }
}
