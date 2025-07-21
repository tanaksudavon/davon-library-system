package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.acme.model.Loan;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LoanRepository implements PanacheRepository<Loan> {
}