package com.zuluflow.ledger.api.controller;

import com.zuluflow.ledger.domain.ledger.Account;
import com.zuluflow.ledger.domain.ledger.LedgerService;
import com.zuluflow.ledger.domain.ledger.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService service;

    // Create a new account
    // POST /api/v1/ledger/accounts?currency=ZAR
    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestParam(defaultValue = "ZAR") String currency) {
        return ResponseEntity.ok(service.createAccount(currency));
    }

    // Check Balance
    // GET /api/v1/ledger/accounts/{accountNumber}
    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(service.getAccount(accountNumber));
    }

    // Transfer Money
    // POST /api/v1/ledger/transactions
    @PostMapping("/transactions")
    public ResponseEntity<Transaction> transfer(@RequestBody TransferRequest request) {
        Transaction tx = service.transferFunds(
                request.fromAccount(),
                request.toAccount(),
                request.amount(),
                request.reference()
        );
        return Respocd ..
        nseEntity.ok(tx);
    }
}

// Simple DTO (Data Transfer Object) for the JSON body
record TransferRequest(String fromAccount, String toAccount, BigDecimal amount, String reference) {}