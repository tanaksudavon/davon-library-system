package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.acme.model.Loan;
import org.acme.model.LoanStatus;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class LoanRepository implements PanacheRepository<Loan> {

    /**
     * Finds all loans that are overdue as of a given date.
     * A loan is considered overdue if its due date is before the specified date
     * and it has not yet been returned.
     *
     * @param date The date to check against.
     * @return A list of overdue loans.
     */
    public List<Loan> findOverdueLoans(LocalDate date) {
        return list("dueDate < ?1 and status != ?2", date, LoanStatus.RETURNED);
    }

    /**
     * Finds all loans borrowed within a specific date range.
     *
     * @param startDate The start of the date range.
     * @param endDate   The end of the date range.
     * @return A list of loans within the date range.
     */
    public List<Loan> findLoansBetween(LocalDate startDate, LocalDate endDate) {
        return list("borrowDate >= ?1 and borrowDate <= ?2", startDate, endDate);
    }

    /**
     * Finds the most popular books based on the number of loans.
     *
     * @param limit The maximum number of popular books to return.
     * @return A list of object arrays, where each array contains a Book and its
     *         loan count.
     */
    public List<Object[]> findPopularBooks(int limit) {
        return getEntityManager().createQuery(
                "select l.book, count(l) from Loan l group by l.book order by count(l) desc", Object[].class)
                .setMaxResults(limit)
                .getResultList();
    }
}