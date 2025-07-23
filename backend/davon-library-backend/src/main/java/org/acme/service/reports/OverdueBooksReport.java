package org.acme.service.reports;

import java.time.LocalDate;
import java.util.List;

public class OverdueBooksReport {
    private List<OverdueBookInfo> overdueBooks;
    private LocalDate reportDate;

    public OverdueBooksReport(List<OverdueBookInfo> overdueBooks, LocalDate reportDate) {
        this.overdueBooks = overdueBooks;
        this.reportDate = reportDate;
    }

    // Getters
    public List<OverdueBookInfo> getOverdueBooks() {
        return overdueBooks;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }
}