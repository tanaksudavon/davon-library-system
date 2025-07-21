package org.acme.service;

import org.acme.model.Loan;
import org.acme.repository.LoanRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LoanService {

    @Inject
    private LoanRepository loanRepository;

    public List<Loan> getAllLoans() {
        return loanRepository.listAll();
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findByIdOptional(id);
    }

    public List<Loan> getLoansByUserId(Long userId) {
        return loanRepository.list("user.id", userId);
    }

    public Loan createLoan(Loan loan) {
        loanRepository.persist(loan);
        return loan;
    }

    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }
}