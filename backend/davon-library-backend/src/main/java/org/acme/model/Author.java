package org.acme.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an author in the library system.
 * Extends BaseEntity to inherit common properties like id and timestamps.
 * 
 * @author Davon Library System
 * @version 1.0
 */
public class Author extends BaseEntity {
    
    /**
     * The author's first name
     */
    private String firstName;
    
    /**
     * The author's last name
     */
    private String lastName;
    
    /**
     * The author's date of birth
     */
    private LocalDate dateOfBirth;
    
    /**
     * The author's nationality
     */
    private String nationality;
    
    /**
     * Brief biography of the author
     */
    private String biography;
    
    /**
     * List of books written by this author
     */
    private List<Book> books;
    
    /**
     * Default constructor
     */
    public Author() {
        super();
        this.books = new ArrayList<>();
    }
    
    /**
     * Constructor with basic author information
     * 
     * @param firstName the author's first name
     * @param lastName the author's last name
     */
    public Author(String firstName, String lastName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    /**
     * Constructor with full author information
     * 
     * @param firstName the author's first name
     * @param lastName the author's last name
     * @param dateOfBirth the author's date of birth
     * @param nationality the author's nationality
     */
    public Author(String firstName, String lastName, LocalDate dateOfBirth, String nationality) {
        this(firstName, lastName);
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
    }
    
    // Getters and Setters
    
    /**
     * Gets the author's first name
     * 
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Sets the author's first name
     * 
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateTimestamp();
    }
    
    /**
     * Gets the author's last name
     * 
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Sets the author's last name
     * 
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateTimestamp();
    }
    
    /**
     * Gets the author's full name
     * 
     * @return the full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Gets the author's date of birth
     * 
     * @return the date of birth
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    /**
     * Sets the author's date of birth
     * 
     * @param dateOfBirth the date of birth to set
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        updateTimestamp();
    }
    
    /**
     * Gets the author's nationality
     * 
     * @return the nationality
     */
    public String getNationality() {
        return nationality;
    }
    
    /**
     * Sets the author's nationality
     * 
     * @param nationality the nationality to set
     */
    public void setNationality(String nationality) {
        this.nationality = nationality;
        updateTimestamp();
    }
    
    /**
     * Gets the author's biography
     * 
     * @return the biography
     */
    public String getBiography() {
        return biography;
    }
    
    /**
     * Sets the author's biography
     * 
     * @param biography the biography to set
     */
    public void setBiography(String biography) {
        this.biography = biography;
        updateTimestamp();
    }
    
    /**
     * Gets the list of books by this author
     * 
     * @return the list of books
     */
    public List<Book> getBooks() {
        return new ArrayList<>(books); // Return defensive copy
    }
    
    /**
     * Adds a book to the author's book list
     * 
     * @param book the book to add
     */
    public void addBook(Book book) {
        if (book != null && !books.contains(book)) {
            books.add(book);
            book.setAuthor(this);
            updateTimestamp();
        }
    }
    
    /**
     * Removes a book from the author's book list
     * 
     * @param book the book to remove
     */
    public void removeBook(Book book) {
        if (books.remove(book)) {
            updateTimestamp();
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Author author = (Author) o;
        return Objects.equals(firstName, author.firstName) && 
               Objects.equals(lastName, author.lastName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName);
    }
    
    @Override
    public String toString() {
        return "Author{" +
                "id=" + getId() +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", nationality='" + nationality + '\'' +
                '}';
    }
} 