package com.zuluflow.beneficiary.domain.beneficiary;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficiaryService {

    private final BeneficiaryRepository repository;

    @Transactional
    public Beneficiary createBeneficiary(Beneficiary beneficiary) {
        // Rule: Prevent duplicates for the same client
        if (repository.findByClientIdAndAccountNumber(beneficiary.getClientId(), beneficiary.getAccountNumber()).isPresent()) {
            throw new IllegalArgumentException("Beneficiary with this account number already exists.");
        }
        // Rule: Always start as PENDING
        beneficiary.setStatus(BeneficiaryStatus.PENDING);
        return repository.save(beneficiary);
    }

    public List<Beneficiary> getBeneficiaries(String clientId) {
        return repository.findAllByClientId(clientId);
    }

    public Beneficiary getBeneficiary(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Beneficiary not found"));
    }
}