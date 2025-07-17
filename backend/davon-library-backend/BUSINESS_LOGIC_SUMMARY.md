# Library Management System - Business Logic Implementation

## Overview
This document summarizes the business logic services implemented for the Davon Library Management System. The implementation follows object-oriented programming principles and includes comprehensive service layers for managing library operations.

## Implemented Services

### 1. BookService (`org.acme.service.BookService`)
**Purpose**: Manages book operations including checkout, return, and inventory management.

**Key Features**:
- **Book Checkout/Return Process**:
  - `checkoutBook(bookId, userId)` - Checks out a book to a user with validation
  - `returnBook(loanId)` - Returns a book and updates status
  - `renewLoan(loanId)` - Renews a loan if eligible
  - Validates user status, borrowing limits, and overdue books

- **Inventory Management**:
  - `addBook(book)` - Adds new books to inventory with validation
  - `updateBook(book)` - Updates existing book information
  - `removeBook(bookId)` - Removes books (if not checked out)
  - `getInventoryStats()` - Returns inventory statistics

- **Book Retrieval**:
  - `getAllBooks()` - Gets all books in inventory
  - `getAvailableBooks()` - Gets only available books
  - `getBooksByStatus(status)` - Filters books by status
  - `findBookById(id)` and `findBookByIsbn(isbn)` - Book lookup

### 2. UserService (`org.acme.service.UserService`)
**Purpose**: Manages user accounts and user-related operations.

**Key Features**:
- **User Management**:
  - `createUser(user)` - Creates new user accounts with validation
  - `updateUser(user)` - Updates existing user information
  - `removeUser(userId)` - Removes users (if no active loans)

- **User Status Management**:
  - `suspendUser(userId)` - Suspends user accounts
  - `activateUser(userId)` - Activates user accounts
  - `blockUser(userId)` - Blocks user accounts
  - `expireUser(userId)` - Expires user accounts

- **User Retrieval**:
  - `getAllUsers()` - Gets all users
  - `getUsersByStatus(status)` - Filters users by status
  - `getUsersByType(userType)` - Filters users by type
  - `getUsersWithOverdueBooks()` - Gets users with overdue items
  - `searchUsersByName(name)` - Search users by name

### 3. LoanService (`org.acme.service.LoanService`)
**Purpose**: Manages loan operations and loan-related data.

**Key Features**:
- **Loan Management**:
  - `createLoan(user, book)` - Creates new loan records
  - `findLoanById(id)` - Retrieves loan by ID
  - `removeLoan(loanId)` - Removes loan records

- **Loan Retrieval**:
  - `getAllLoans()` - Gets all loans
  - `getActiveLoans()` - Gets currently active loans
  - `getOverdueLoans()` - Gets overdue loans
  - `getLoansByUser(userId)` - Gets loans for specific user
  - `getLoansByBook(bookId)` - Gets loans for specific book
  - `getLoansDueToday()` - Gets loans due today
  - `getLoansDueSoon()` - Gets loans due within 3 days

- **Analytics**:
  - `getLoanStats()` - Returns loan statistics
  - `calculateTotalFines()` - Calculates total fine amounts

### 4. SearchService (`org.acme.service.SearchService`)
**Purpose**: Provides comprehensive search functionality across the library system.

**Key Features**:
- **Book Search**:
  - `searchBooksByTitle(title)` - Search books by title
  - `searchBooksByAuthor(author)` - Search books by author name
  - `searchBooksByGenre(genre)` - Search books by genre
  - `searchBooksByPublisher(publisher)` - Search books by publisher
  - `searchBooksByIsbn(isbn)` - Search books by ISBN
  - `searchBooks(searchTerm)` - General search across all book fields
  - `searchAvailableBooks(searchTerm)` - Search only available books

- **Advanced Search**:
  - `advancedSearch(title, author, genre, publisher, availableOnly)` - Multi-criteria search

- **User Search**:
  - `searchUsersByName(name)` - Search users by name
  - `searchUsersByEmail(email)` - Search users by email

- **Suggestions & Metadata**:
  - `getBookTitleSuggestions(partial, maxResults)` - Auto-complete for titles
  - `getAuthorNameSuggestions(partial, maxResults)` - Auto-complete for authors
  - `getAllGenres()` - Get all available genres
  - `getAllPublishers()` - Get all publishers

## REST API Endpoints

### Book Checkout/Return Operations
- `POST /library/checkout?bookId={id}&userId={id}` - Checkout book
- `POST /library/return?loanId={id}` - Return book
- `POST /library/renew?loanId={id}` - Renew loan

### Inventory Management
- `POST /library/books` - Add new book
- `PUT /library/books/{id}` - Update book
- `DELETE /library/books/{id}` - Remove book
- `GET /library/books` - Get all books
- `GET /library/books/available` - Get available books
- `GET /library/books/status/{status}` - Get books by status
- `GET /library/inventory/stats` - Get inventory statistics

### User Management
- `POST /library/users` - Create user
- `PUT /library/users/{id}` - Update user
- `POST /library/users/{id}/suspend` - Suspend user
- `POST /library/users/{id}/activate` - Activate user
- `GET /library/users` - Get all users
- `GET /library/users/status/{status}` - Get users by status
- `GET /library/users/overdue` - Get users with overdue books
- `GET /library/users/stats` - Get user statistics

### Loan Management
- `GET /library/loans` - Get all loans
- `GET /library/loans/active` - Get active loans
- `GET /library/loans/overdue` - Get overdue loans
- `GET /library/loans/user/{userId}` - Get loans by user
- `GET /library/loans/due-today` - Get loans due today
- `GET /library/loans/stats` - Get loan statistics

### Search Functionality
- `GET /library/search/books?q={term}` - General book search
- `GET /library/search/books/title?q={term}` - Search by title
- `GET /library/search/books/author?q={term}` - Search by author
- `GET /library/search/books/genre?q={term}` - Search by genre
- `GET /library/search/books/available?q={term}` - Search available books
- `GET /library/search/books/advanced?title={}&author={}&genre={}&publisher={}&availableOnly={}` - Advanced search
- `GET /library/search/users?q={term}` - Search users
- `GET /library/genres` - Get all genres
- `GET /library/publishers` - Get all publishers

### Demo Data
- `POST /library/demo/init` - Initialize demo data for testing

## Business Rules Implemented

### User Borrowing Rules
- Users can only borrow books if their account is ACTIVE
- Users cannot exceed their maximum borrowing limit (varies by user type)
- Users with overdue books cannot checkout new books
- Different user types have different borrowing limits:
  - STUDENT: 5 books
  - FACULTY: 10 books
  - STAFF: 7 books
  - PUBLIC: 3 books

### Book Checkout Rules
- Books must be AVAILABLE to be checked out
- Books automatically change status to CHECKED_OUT when borrowed
- Books return to AVAILABLE status when returned

### Loan Rules
- Default loan period: 14 days
- Maximum renewals: 2 per loan
- Renewals only allowed if not overdue
- Daily fine rate: $0.50 for overdue books
- Loans automatically marked as OVERDUE when past due date

### Inventory Rules
- Books cannot be removed if currently checked out
- ISBN must be unique across all books
- Required fields: title, ISBN for books; firstName, lastName, email for users

## Data Storage
- Currently uses in-memory storage (HashMap) for demonstration
- Designed to be easily replaceable with database persistence
- All services are stateless and thread-safe

## Error Handling
- Comprehensive validation with descriptive error messages
- Proper HTTP status codes in REST responses
- Business rule violations throw appropriate exceptions

## Testing
- All services compile successfully
- Maven build and test suite passes
- Ready for unit test implementation

This implementation provides a solid foundation for a library management system with proper separation of concerns, comprehensive business logic, and extensive API coverage. 