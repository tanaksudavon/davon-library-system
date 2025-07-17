package org.acme.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a book in the library system.
 * Extends BaseEntity to inherit common properties like id and timestamps.
 * 
 * @author Davon Library System
 * @version 1.0
 */
public class Book extends BaseEntity {
    
    /**
     * The book's title
     */
    private String title;
    
    /**
     * The book's ISBN (International Standard Book Number)
     */
    private String isbn;
    
    /**
     * The author of the book
     */
    private Author author;
    
    /**
     * The publisher of the book
     */
    private String publisher;
    
    /**
     * The publication date
     */
    private LocalDate publicationDate;
    
    /**
     * The genre or category of the book
     */
    private String genre;
    
    /**
     * Brief description of the book
     */
    private String description;
    
    /**
     * Number of pages in the book
     */
    private Integer pageCount;
    
    /**
     * Current availability status of the book
     */
    private BookStatus status;
    
    /**
     * The location of the book in the library (shelf number, etc.)
     */
    private String location;
    
    /**
     * Enum representing the availability status of a book
     */
    public enum BookStatus {
        AVAILABLE,
        CHECKED_OUT,
        RESERVED,
        MAINTENANCE,
        LOST
    }
    
    /**
     * Default constructor
     */
    public Book() {
        super();
        this.status = BookStatus.AVAILABLE;
    }
    
    /**
     * Constructor with basic book information
     * 
     * @param title the book's title
     * @param isbn the book's ISBN
     * @param author the book's author
     */
    public Book(String title, String isbn, Author author) {
        this();
        this.title = title;
        this.isbn = isbn;
        this.author = author;
    }
    
    /**
     * Constructor with extended book information
     * 
     * @param title the book's title
     * @param isbn the book's ISBN
     * @param author the book's author
     * @param publisher the publisher
     * @param publicationDate the publication date
     * @param genre the genre
     */
    public Book(String title, String isbn, Author author, String publisher, 
                LocalDate publicationDate, String genre) {
        this(title, isbn, author);
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.genre = genre;
    }
    
    // Getters and Setters
    
    /**
     * Gets the book's title
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the book's title
     * 
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
        updateTimestamp();
    }
    
    /**
     * Gets the book's ISBN
     * 
     * @return the ISBN
     */
    public String getIsbn() {
        return isbn;
    }
    
    /**
     * Sets the book's ISBN
     * 
     * @param isbn the ISBN to set
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
        updateTimestamp();
    }
    
    /**
     * Gets the book's author
     * 
     * @return the author
     */
    public Author getAuthor() {
        return author;
    }
    
    /**
     * Sets the book's author
     * 
     * @param author the author to set
     */
    public void setAuthor(Author author) {
        this.author = author;
        updateTimestamp();
    }
    
    /**
     * Gets the publisher
     * 
     * @return the publisher
     */
    public String getPublisher() {
        return publisher;
    }
    
    /**
     * Sets the publisher
     * 
     * @param publisher the publisher to set
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
        updateTimestamp();
    }
    
    /**
     * Gets the publication date
     * 
     * @return the publication date
     */
    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    
    /**
     * Sets the publication date
     * 
     * @param publicationDate the publication date to set
     */
    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
        updateTimestamp();
    }
    
    /**
     * Gets the genre
     * 
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }
    
    /**
     * Sets the genre
     * 
     * @param genre the genre to set
     */
    public void setGenre(String genre) {
        this.genre = genre;
        updateTimestamp();
    }
    
    /**
     * Gets the description
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description
     * 
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
        updateTimestamp();
    }
    
    /**
     * Gets the page count
     * 
     * @return the page count
     */
    public Integer getPageCount() {
        return pageCount;
    }
    
    /**
     * Sets the page count
     * 
     * @param pageCount the page count to set
     */
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
        updateTimestamp();
    }
    
    /**
     * Gets the current status
     * 
     * @return the status
     */
    public BookStatus getStatus() {
        return status;
    }
    
    /**
     * Sets the current status
     * 
     * @param status the status to set
     */
    public void setStatus(BookStatus status) {
        this.status = status;
        updateTimestamp();
    }
    
    /**
     * Gets the location
     * 
     * @return the location
     */
    public String getLocation() {
        return location;
    }
    
    /**
     * Sets the location
     * 
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
        updateTimestamp();
    }
    
    /**
     * Checks if the book is available for checkout
     * 
     * @return true if the book is available, false otherwise
     */
    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }
    
    /**
     * Marks the book as checked out
     */
    public void checkOut() {
        if (status == BookStatus.AVAILABLE) {
            this.status = BookStatus.CHECKED_OUT;
            updateTimestamp();
        }
    }
    
    /**
     * Marks the book as returned/available
     */
    public void returnBook() {
        if (status == BookStatus.CHECKED_OUT) {
            this.status = BookStatus.AVAILABLE;
            updateTimestamp();
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isbn);
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", isbn='" + isbn + '\'' +
                ", author=" + (author != null ? author.getFullName() : "Unknown") +
                ", publisher='" + publisher + '\'' +
                ", status=" + status +
                '}';
    }
} 