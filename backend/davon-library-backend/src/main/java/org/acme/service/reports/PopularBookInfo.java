package org.acme.service.reports;

public class PopularBookInfo {
    private String title;
    private String isbn;
    private int loanCount;

    public PopularBookInfo(String title, String isbn, int loanCount) {
        this.title = title;
        this.isbn = isbn;
        this.loanCount = loanCount;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getLoanCount() {
        return loanCount;
    }
}