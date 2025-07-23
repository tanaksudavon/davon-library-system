package org.acme.service.reports;

public class MonthlyLoanReport {
    private int year;
    private int month;
    private int totalLoans;
    private int returnedLoans;
    private int overdueLoans;
    private double returnRate;

    public MonthlyLoanReport(int year, int month, int totalLoans, int returnedLoans, int overdueLoans,
            double returnRate) {
        this.year = year;
        this.month = month;
        this.totalLoans = totalLoans;
        this.returnedLoans = returnedLoans;
        this.overdueLoans = overdueLoans;
        this.returnRate = returnRate;
    }

    // Getters
    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getTotalLoans() {
        return totalLoans;
    }

    public int getReturnedLoans() {
        return returnedLoans;
    }

    public int getOverdueLoans() {
        return overdueLoans;
    }

    public double getReturnRate() {
        return returnRate;
    }
}