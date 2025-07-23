package org.acme.service.reports;

import java.math.BigDecimal;

public class UserActivityReport {
    private Long userId;
    private String username;
    private int totalLoans;
    private int activeLoans;
    private BigDecimal totalFines;

    public UserActivityReport(Long userId, String username, int totalLoans, int activeLoans, BigDecimal totalFines) {
        this.userId = userId;
        this.username = username;
        this.totalLoans = totalLoans;
        this.activeLoans = activeLoans;
        this.totalFines = totalFines;
    }

    // Getters
    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getTotalLoans() {
        return totalLoans;
    }

    public int getActiveLoans() {
        return activeLoans;
    }

    public BigDecimal getTotalFines() {
        return totalFines;
    }
}