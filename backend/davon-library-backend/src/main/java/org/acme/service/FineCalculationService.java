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

/**
 * Service for handling fine calculations and payments.
 * This service provides methods to calculate, retrieve, and pay fines for
 * overdue books.
 */
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

    /**
     * Calculates and persists fines for all overdue loans.
     * This method iterates through all non-returned loans, calculates fines for
     * overdue ones,
     * and creates new fine records if they don't already exist.
     *
     * @return A list of newly created fines.
     */
    @Transactional
    public List<Fine> calculateOverdueFines() {
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDate.now());
        List<Fine> newFines = new ArrayList<>();

        for (Loan loan : overdueLoans) {
            if (loan == null || loan.getDueDate() == null) {
                continue; // Skip invalid loan data
            }

            findExistingFineForLoan(loan.getId()).ifPresentOrElse(
                    existingFine -> {
                        // Fine already exists, potentially update it if logic changes
                    },
                    () -> {
                        long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
                        if (overdueDays > 0) {
                            BigDecimal fineAmount = calculateFineAmount(overdueDays);
                            Fine newFine = createFineObject(loan, fineAmount);
                            fineRepository.persist(newFine);
                            newFines.add(newFine);
                        }
                    });
        }
        return newFines;
    }

    /**
     * Calculates the fine amount based on the number of overdue days.
     * The fine is capped at a maximum value.
     *
     * @param overdueDays The number of days a book is overdue.
     * @return The calculated fine amount as a BigDecimal.
     */
    private BigDecimal calculateFineAmount(long overdueDays) {
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalFine = DAILY_FINE_RATE.multiply(BigDecimal.valueOf(overdueDays));
        return totalFine.compareTo(MAXIMUM_FINE) > 0 ? MAXIMUM_FINE : totalFine;
    }

    /**
     * Retrieves the total outstanding fines for a specific user.
     *
     * @param userId The ID of the user.
     * @return The total sum of unpaid fines as a BigDecimal.
     */
    public BigDecimal getTotalFinesForUser(Long userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        List<Fine> userFines = fineRepository.findByUserIdAndStatus(userId, FineStatus.PENDING);

        return userFines.stream()
                .map(Fine::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Marks a fine as paid.
     *
     * @param fineId The ID of the fine to be paid.
     * @return The updated Fine object.
     * @throws NotFoundException if the fine with the given ID is not found.
     */
    @Transactional
    public Fine payFine(Long fineId) {
        Objects.requireNonNull(fineId, "Fine ID cannot be null");
        Fine fine = fineRepository.findByIdOptional(fineId)
                .orElseThrow(() -> new NotFoundException("Fine not found with ID: " + fineId));

        fine.setStatus(FineStatus.PAID);
        fine.setPaidDate(LocalDate.now());
        fineRepository.persist(fine);
        return fine;
    }

    /**
     * Finds an existing fine for a given loan ID.
     *
     * @param loanId The ID of the loan.
     * @return An Optional containing the fine if found, otherwise empty.
     */
    private Optional<Fine> findExistingFineForLoan(Long loanId) {
        return fineRepository.findByLoanId(loanId).stream().findFirst();
    }

    /**
     * Helper method to create a new Fine object.
     *
     * @param loan   The loan for which the fine is being created.
     * @param amount The amount of the fine.
     * @return A new Fine object, ready to be persisted.
     */
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