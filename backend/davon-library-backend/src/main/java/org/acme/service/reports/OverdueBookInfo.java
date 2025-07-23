package org.acme.service.reports;

import java.time.LocalDate;

public class OverdueBookInfo {
    private String title;
    private String isbn;
    private String username;
    private String email;
    private LocalDate dueDate;
    private int daysOverdue;

    public OverdueBookInfo(String title, String isbn, String username, String email, LocalDate dueDate,
            int daysOverdue) {
        this.title = title;
        this.isbn = isbn;
        this.username = username;
        this.email = email;
        this.dueDate = dueDate;
        this.daysOverdue = daysOverdue;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public int getDaysOverdue() {
        return daysOverdue;
    }
}