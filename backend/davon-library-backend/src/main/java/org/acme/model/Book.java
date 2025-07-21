package org.acme.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String isbn;
    private String description;
    private LocalDate publishDate;
    private String coverImage;
    @Enumerated(EnumType.STRING)
    private BookStatus status;
    private Long authorId;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and setters
}