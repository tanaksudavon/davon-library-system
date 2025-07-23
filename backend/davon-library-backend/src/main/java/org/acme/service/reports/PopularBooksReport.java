package org.acme.service.reports;

import java.util.List;

public class PopularBooksReport {
    private List<PopularBookInfo> popularBooks;

    public PopularBooksReport(List<PopularBookInfo> popularBooks) {
        this.popularBooks = popularBooks;
    }

    // Getter
    public List<PopularBookInfo> getPopularBooks() {
        return popularBooks;
    }
}