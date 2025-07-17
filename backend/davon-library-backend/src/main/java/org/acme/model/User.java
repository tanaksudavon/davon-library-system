package org.acme.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a library user in the system.
 * Extends BaseEntity to inherit common properties like id and timestamps.
 * 
 * @author Davon Library System
 * @version 1.0
 */
public class User extends BaseEntity {
    
    /**
     * The user's first name
     */
    private String firstName;
    
    /**
     * The user's last name
     */
    private String lastName;
    
    /**
     * The user's email address (unique identifier)
     */
    private String email;
    
    /**
     * The user's phone number
     */
    private String phoneNumber;
    
    /**
     * The user's address
     */
    private String address;
    
    /**
     * The user's date of birth
     */
    private LocalDate dateOfBirth;
    
    /**
     * The user's membership start date
     */
    private LocalDate membershipDate;
    
    /**
     * The type of user membership
     */
    private UserType userType;
    
    /**
     * Current status of the user account
     */
    private UserStatus status;
    
    /**
     * List of current loans for this user
     */
    private List<Loan> currentLoans;
    
    /**
     * List of loan history for this user
     */
    private List<Loan> loanHistory;
    
    /**
     * Maximum number of books this user can borrow simultaneously
     */
    private Integer maxBooksAllowed;
    
    /**
     * Enum representing different types of library users
     */
    public enum UserType {
        STUDENT(5),
        FACULTY(10),
        STAFF(7),
        PUBLIC(3);
        
        private final int defaultMaxBooks;
        
        UserType(int defaultMaxBooks) {
            this.defaultMaxBooks = defaultMaxBooks;
        }
        
        public int getDefaultMaxBooks() {
            return defaultMaxBooks;
        }
    }
    
    /**
     * Enum representing user account status
     */
    public enum UserStatus {
        ACTIVE,
        SUSPENDED,
        EXPIRED,
        BLOCKED
    }
    
    /**
     * Default constructor
     */
    public User() {
        super();
        this.currentLoans = new ArrayList<>();
        this.loanHistory = new ArrayList<>();
        this.status = UserStatus.ACTIVE;
        this.membershipDate = LocalDate.now();
        this.userType = UserType.PUBLIC;
        this.maxBooksAllowed = userType.getDefaultMaxBooks();
    }
    
    /**
     * Constructor with basic user information
     * 
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email address
     */
    public User(String firstName, String lastName, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    /**
     * Constructor with extended user information
     * 
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email address
     * @param phoneNumber the user's phone number
     * @param userType the type of user
     */
    public User(String firstName, String lastName, String email, 
                String phoneNumber, UserType userType) {
        this(firstName, lastName, email);
        this.phoneNumber = phoneNumber;
        this.userType = userType;
        this.maxBooksAllowed = userType.getDefaultMaxBooks();
    }
    
    // Getters and Setters
    
    /**
     * Gets the user's first name
     * 
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Sets the user's first name
     * 
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateTimestamp();
    }
    
    /**
     * Gets the user's last name
     * 
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Sets the user's last name
     * 
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateTimestamp();
    }
    
    /**
     * Gets the user's full name
     * 
     * @return the full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Gets the user's email address
     * 
     * @return the email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the user's email address
     * 
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
        updateTimestamp();
    }
    
    /**
     * Gets the user's phone number
     * 
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    /**
     * Sets the user's phone number
     * 
     * @param phoneNumber the phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        updateTimestamp();
    }
    
    /**
     * Gets the user's address
     * 
     * @return the address
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Sets the user's address
     * 
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
        updateTimestamp();
    }
    
    /**
     * Gets the user's date of birth
     * 
     * @return the date of birth
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    /**
     * Sets the user's date of birth
     * 
     * @param dateOfBirth the date of birth to set
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        updateTimestamp();
    }
    
    /**
     * Gets the membership date
     * 
     * @return the membership date
     */
    public LocalDate getMembershipDate() {
        return membershipDate;
    }
    
    /**
     * Sets the membership date
     * 
     * @param membershipDate the membership date to set
     */
    public void setMembershipDate(LocalDate membershipDate) {
        this.membershipDate = membershipDate;
        updateTimestamp();
    }
    
    /**
     * Gets the user type
     * 
     * @return the user type
     */
    public UserType getUserType() {
        return userType;
    }
    
    /**
     * Sets the user type
     * 
     * @param userType the user type to set
     */
    public void setUserType(UserType userType) {
        this.userType = userType;
        this.maxBooksAllowed = userType.getDefaultMaxBooks();
        updateTimestamp();
    }
    
    /**
     * Gets the user status
     * 
     * @return the user status
     */
    public UserStatus getStatus() {
        return status;
    }
    
    /**
     * Sets the user status
     * 
     * @param status the user status to set
     */
    public void setStatus(UserStatus status) {
        this.status = status;
        updateTimestamp();
    }
    
    /**
     * Gets the current loans
     * 
     * @return defensive copy of current loans list
     */
    public List<Loan> getCurrentLoans() {
        return new ArrayList<>(currentLoans);
    }
    
    /**
     * Adds a loan to current loans
     * 
     * @param loan the loan to add
     */
    public void addCurrentLoan(Loan loan) {
        if (loan != null && !currentLoans.contains(loan)) {
            currentLoans.add(loan);
            updateTimestamp();
        }
    }
    
    /**
     * Removes a loan from current loans and adds to history
     * 
     * @param loan the loan to remove
     */
    public void returnLoan(Loan loan) {
        if (currentLoans.remove(loan)) {
            loanHistory.add(loan);
            updateTimestamp();
        }
    }
    
    /**
     * Gets the loan history
     * 
     * @return defensive copy of loan history list
     */
    public List<Loan> getLoanHistory() {
        return new ArrayList<>(loanHistory);
    }
    
    /**
     * Gets the maximum books allowed
     * 
     * @return the maximum books allowed
     */
    public Integer getMaxBooksAllowed() {
        return maxBooksAllowed;
    }
    
    /**
     * Sets the maximum books allowed
     * 
     * @param maxBooksAllowed the maximum books allowed to set
     */
    public void setMaxBooksAllowed(Integer maxBooksAllowed) {
        this.maxBooksAllowed = maxBooksAllowed;
        updateTimestamp();
    }
    
    /**
     * Checks if the user can borrow more books
     * 
     * @return true if user can borrow more books, false otherwise
     */
    public boolean canBorrowMore() {
        return status == UserStatus.ACTIVE && 
               currentLoans.size() < maxBooksAllowed;
    }
    
    /**
     * Gets the number of books currently borrowed
     * 
     * @return the number of current loans
     */
    public int getCurrentBooksCount() {
        return currentLoans.size();
    }
    
    /**
     * Checks if the user account is active
     * 
     * @return true if user is active, false otherwise
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), email);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", userType=" + userType +
                ", status=" + status +
                ", currentLoans=" + currentLoans.size() +
                '}';
    }
} 