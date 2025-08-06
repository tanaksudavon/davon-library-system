package org.acme.service;

import org.acme.model.Fine;
import org.acme.model.FineStatus;
import org.acme.model.Loan;
import org.acme.repository.FineRepository;
import org.acme.repository.LoanRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class FineCalculationService {

    private final LoanRepository loanRepository;
    private final FineRepository fineRepository;

    private static final BigDecimal DAILY_FINE_RATE = new BigDecimal("0.50");
    private static final BigDecimal MAXIMUM_FINE = new BigDecimal("25.00");

    @Inject
    public FineCalculationService(LoanRepository loanRepository, FineRepository fineRepository) {
        this.loanRepository = loanRepository;
        this.fineRepository = fineRepository;
    }

    @Transactional
    public List<Fine> calculateOverdueFines() {
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDate.now());
        List<Fine> updatedOrCreatedFines = new ArrayList<>();

        for (Loan loan : overdueLoans) {
            if (loan == null || loan.getDueDate() == null) {
                continue; // Skip invalid loan data
            }

            long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
            if (overdueDays <= 0) {
                continue; // Not overdue
            }

            BigDecimal newFineAmount = calculateFineAmount(overdueDays);

            Optional<Fine> existingFineOpt = findExistingFineForLoan(loan.getId());

            if (existingFineOpt.isPresent()) {
                Fine existingFine = existingFineOpt.get();
                updateExistingFine(existingFine, newFineAmount, updatedOrCreatedFines);
            } else {
                Fine newFine = createFineObject(loan, newFineAmount);
                fineRepository.persist(newFine);
                updatedOrCreatedFines.add(newFine);
            }
        }
        return updatedOrCreatedFines;
    }

    private void updateExistingFine(Fine existingFine, BigDecimal newFineAmount, List<Fine> updatedFines) {
        if (existingFine.getStatus() == FineStatus.PENDING && newFineAmount.compareTo(existingFine.getAmount()) > 0) {
            existingFine.setAmount(newFineAmount);
            fineRepository.persist(existingFine);
            updatedFines.add(existingFine);
        }
    }

    private BigDecimal calculateFineAmount(long overdueDays) {
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalFine = DAILY_FINE_RATE.multiply(BigDecimal.valueOf(overdueDays));
        return totalFine.compareTo(MAXIMUM_FINE) > 0 ? MAXIMUM_FINE : totalFine;
    }

    public BigDecimal getTotalFinesForUser(Long userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        List<Fine> userFines = fineRepository.findByUserIdAndStatus(userId, FineStatus.PENDING);

        return userFines.stream()
                .map(Fine::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public Fine payFine(Long fineId) {
        Objects.requireNonNull(fineId, "Fine ID cannot be null");
        Fine fine = fineRepository.findFineByIdWithDetails(fineId)
                .orElseThrow(() -> new NotFoundException("Fine not found with ID: " + fineId));

        fine.setStatus(FineStatus.PAID);
        fine.setPaidDate(LocalDate.now());
        fineRepository.persist(fine);
        return fine;
    }

    private Optional<Fine> findExistingFineForLoan(Long loanId) {
        return fineRepository.findByLoanId(loanId).stream().findFirst();
    }

    private Fine createFineObject(Loan loan, BigDecimal amount) {
        Fine fine = new Fine();
        fine.setLoan(loan);
        fine.setUser(loan.getUser());
        fine.setAmount(amount);
        fine.setReason("Overdue book fine for book: " + loan.getBook().getTitle());
        fine.setStatus(FineStatus.PENDING);
        fine.setIssuedDate(LocalDate.now());
        return fine;
    }
}
