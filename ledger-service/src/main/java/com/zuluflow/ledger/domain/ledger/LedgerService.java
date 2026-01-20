package com.zuluflow.ledger.domain.ledger;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    // 1. Create a Wallet (Starts with 0.00)
    public Account createAccount(String currency) {
        Account account = Account.builder()
                .accountNumber(generateAccountNumber()) // Logic below
                .currency(currency)
                .balance(BigDecimal.ZERO) // Always start empty
                .status(AccountStatus.ACTIVE)
                .build();
        return accountRepository.save(account);
    }

    // 2. The Big One: TRANSFER FUNDS (Double Entry)
    @Transactional // <--- CRITICAL: If any line fails, EVERYTHING rolls back.
    public Transaction transferFunds(String fromAccountNum, String toAccountNum, BigDecimal amount, String reference) {
        // A. Validation
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (fromAccountNum.equals(toAccountNum)) {
            throw new IllegalArgumentException("Cannot transfer to self");
        }

        // B. Load Accounts (Locking happens here via JPA Optimistic Lock)
        Account source = getAccount(fromAccountNum);
        Account target = getAccount(toAccountNum);

        // C. Check Balance
        if (source.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        // D. The Movement (Memory only)
        source.setBalance(source.getBalance().subtract(amount)); // DEBIT
        target.setBalance(target.getBalance().add(amount));      // CREDIT

        // E. Save Accounts (Database Update)
        accountRepository.save(source);
        accountRepository.save(target);

        // F. Create the Permanent Record
        Transaction record = Transaction.builder()
                .amount(amount)
                .debitAccountNumber(fromAccountNum)
                .creditAccountNumber(toAccountNum)
                .reference(reference)
                .build();

        return transactionRepository.save(record);
    }

    // Helper: Find or Fail
    public Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException("Account not found: " + accountNumber));
    }

    // Helper: Fake IBAN generator
    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}