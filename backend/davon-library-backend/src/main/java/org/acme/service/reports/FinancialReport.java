package org.acme.service.reports;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinancialReport {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalFinesIssued;
    private BigDecimal totalFinesPaid;
    private BigDecimal outstandingFines;

    public FinancialReport(LocalDate startDate, LocalDate endDate, BigDecimal totalFinesIssued,
            BigDecimal totalFinesPaid, BigDecimal outstandingFines) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalFinesIssued = totalFinesIssued;
        this.totalFinesPaid = totalFinesPaid;
        this.outstandingFines = outstandingFines;
    }

    // Getters
    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getTotalFinesIssued() {
        return totalFinesIssued;
    }

    public BigDecimal getTotalFinesPaid() {
        return totalFinesPaid;
    }

    public BigDecimal getOutstandingFines() {
        return outstandingFines;
    }
}