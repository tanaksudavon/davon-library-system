package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.acme.model.Fine;
import org.acme.model.FineStatus;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@ApplicationScoped
public class FineRepository implements PanacheRepository<Fine> {

    public List<Fine> findByUserId(Long userId) {
        return list("user.id", userId);
    }

    public List<Fine> findByUserIdAndStatus(Long userId, FineStatus status) {
        return list("user.id = ?1 and status = ?2", userId, status);
    }

    public List<Fine> findByLoanId(Long loanId) {
        return list("loan.id", loanId);
    }

    public Optional<Fine> findFineByIdWithDetails(Long fineId) {
        return find("SELECT f FROM Fine f " +
                "LEFT JOIN FETCH f.loan l " +
                "LEFT JOIN FETCH l.book b " +
                "LEFT JOIN FETCH b.author " +
                "LEFT JOIN FETCH b.category " +
                "LEFT JOIN FETCH f.user " +
                "WHERE f.id = ?1", fineId).firstResultOptional();
    }

    public List<Fine> findFinesBetween(LocalDate startDate, LocalDate endDate) {
        return list("issuedDate >= ?1 and issuedDate <= ?2", startDate, endDate);
    }
}
