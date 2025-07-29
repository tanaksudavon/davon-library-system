# Davon Library Management System - Database Documentation

## Overview
This document provides comprehensive documentation for the Davon Library Management System database schema, designed to support a full-featured library management system with user management, book inventory, loans, reservations, and financial tracking.

## Database Schema Overview

### Core Entities
The database consists of 9 main tables organized around core library operations:

1. **Users** - Base table for all system users (librarians, members, guests)
2. **Authors** - Book authors information
3. **Categories** - Book categorization system
4. **Books** - Central book inventory
5. **Addresses** - User address information
6. **Memberships** - Library membership details
7. **Loans** - Book borrowing transactions
8. **Reservations** - Book reservation system
9. **Fines** - Financial penalties and fees

## Entity Relationship Diagram (ERD) Description

### Primary Relationships

```
USERS (1) -----> (M) ADDRESSES
  |
  ├── (1) -----> (M) MEMBERSHIPS
  |
  ├── (1) -----> (M) LOANS
  |
  ├── (1) -----> (M) RESERVATIONS
  |
  └── (1) -----> (M) FINES

AUTHORS (1) -----> (M) BOOKS

CATEGORIES (1) -----> (M) BOOKS

BOOKS (1) -----> (M) LOANS
  |
  └── (1) -----> (M) RESERVATIONS

LOANS (1) -----> (M) FINES
```

### Table Relationships

#### Users Table (Central Hub)
- **Primary Key**: `id` (BIGINT)
- **Relationships**:
  - One-to-Many with `addresses` (user can have multiple addresses)
  - One-to-Many with `memberships` (user can have membership history)
  - One-to-Many with `loans` (user can borrow multiple books)
  - One-to-Many with `reservations` (user can reserve multiple books)
  - One-to-Many with `fines` (user can have multiple fines)

#### Books Table (Core Inventory)
- **Primary Key**: `id` (BIGINT)
- **Foreign Keys**:
  - `author_id` references `authors(id)`
  - `category_id` references `categories(id)`
- **Relationships**:
  - Many-to-One with `authors`
  - Many-to-One with `categories`
  - One-to-Many with `loans`
  - One-to-Many with `reservations`

#### Loans Table (Transaction Records)
- **Primary Key**: `id` (BIGINT)
- **Foreign Keys**:
  - `book_id` references `books(id)`
  - `user_id` references `users(id)`
- **Relationships**:
  - Many-to-One with `books`
  - Many-to-One with `users`
  - One-to-Many with `fines`

## Table Specifications

### Users Table
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL CHECK (role IN ('GUEST', 'MEMBER', 'LIBRARIAN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Purpose**: Central user management with role-based access
**Key Features**:
- Unique username and email constraints
- Role-based system (GUEST, MEMBER, LIBRARIAN)
- Automatic timestamp tracking
- Supports inheritance pattern in Java (Member, Librarian, Guest classes)

### Books Table
```sql
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    description TEXT,
    publish_date DATE,
    cover_image VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' 
        CHECK (status IN ('AVAILABLE', 'BORROWED', 'MAINTENANCE', 'RESERVED')),
    author_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE RESTRICT,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
);
```

**Purpose**: Central book inventory management
**Key Features**:
- Status tracking for availability
- ISBN uniqueness constraint
- Foreign key relationships with authors and categories
- Prevent deletion of referenced authors/categories (RESTRICT)

### Loans Table
```sql
CREATE TABLE loans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    borrow_date DATE NOT NULL DEFAULT CURRENT_DATE,
    due_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'BORROWED' 
        CHECK (status IN ('BORROWED', 'RETURNED', 'OVERDUE', 'LOST')),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);
```

**Purpose**: Track book borrowing transactions
**Key Features**:
- Complete loan lifecycle tracking
- Business rule constraints (due_date > borrow_date)
- Status progression from BORROWED to RETURNED/OVERDUE/LOST
- Optional notes for special circumstances

## Business Rules and Constraints

### Data Integrity Constraints
```sql
-- Ensure due date is after borrow date
ALTER TABLE loans ADD CONSTRAINT chk_loan_dates 
    CHECK (due_date > borrow_date);

-- Ensure return date is valid when provided
ALTER TABLE loans ADD CONSTRAINT chk_return_date 
    CHECK (return_date IS NULL OR return_date >= borrow_date);

-- Ensure reservation expiration is after reservation date
ALTER TABLE reservations ADD CONSTRAINT chk_reservation_dates 
    CHECK (expiration_date > reservation_date);

-- Ensure fine amounts are non-negative
ALTER TABLE fines ADD CONSTRAINT chk_fine_amount 
    CHECK (amount >= 0);
```

### Automated Business Logic (Triggers)
```sql
-- Automatically update book status when loaned
CREATE TRIGGER trg_loan_book_status
AFTER INSERT ON loans
FOR EACH ROW
UPDATE books SET status = 'BORROWED' WHERE id = NEW.book_id;

-- Automatically update book status when returned
CREATE TRIGGER trg_return_book_status
AFTER UPDATE ON loans
FOR EACH ROW
IF NEW.status = 'RETURNED' AND OLD.status != 'RETURNED' THEN
    UPDATE books SET status = 'AVAILABLE' WHERE id = NEW.book_id;
END IF;

-- Automatically create fines for overdue books
CREATE TRIGGER trg_overdue_fine
AFTER UPDATE ON loans
FOR EACH ROW
IF NEW.status = 'OVERDUE' AND OLD.status != 'OVERDUE' THEN
    INSERT INTO fines (loan_id, user_id, amount, reason, issued_date)
    VALUES (NEW.id, NEW.user_id, 5.00, 'Overdue book fine', CURRENT_DATE);
END IF;
```

## Performance Optimization

### Indexes
Strategic indexes are created for common query patterns:

```sql
-- User lookup indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Book search indexes
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_status ON books(status);
CREATE INDEX idx_books_author ON books(author_id);
CREATE INDEX idx_books_category ON books(category_id);

-- Loan management indexes
CREATE INDEX idx_loans_user ON loans(user_id);
CREATE INDEX idx_loans_book ON loans(book_id);
CREATE INDEX idx_loans_status ON loans(status);
CREATE INDEX idx_loans_due_date ON loans(due_date);
```

### Views for Common Operations
Pre-built views optimize frequent queries:

- `v_available_books` - Books available for borrowing
- `v_current_loans` - Active loan information
- `v_overdue_books` - Overdue books requiring attention
- `v_user_loan_history` - Complete user borrowing history
- `v_popular_books` - Most frequently borrowed books

## Security Considerations

### Data Protection
- **Password Storage**: Passwords should be hashed before storage (handled by application layer)
- **PII Protection**: Personal information (email, phone, address) requires access controls
- **Financial Data**: Fine amounts and payment information need audit trails

### Access Control
- **Role-Based Access**: Use the `role` field for authorization
- **Data Segregation**: Guests have limited access, Members have borrowing rights, Librarians have full administrative access

## Maintenance and Monitoring

### Regular Maintenance Tasks
1. **Archive Old Data**: Move completed loans older than 2 years to archive tables
2. **Index Maintenance**: Rebuild indexes monthly for optimal performance
3. **Statistics Update**: Refresh table statistics for query optimization
4. **Constraint Validation**: Verify all constraints are functioning correctly

### Monitoring Queries
Use the provided health check query for system monitoring:
```sql
SELECT 'Total Users' as metric, COUNT(*) as count FROM users
UNION ALL SELECT 'Available Books', COUNT(*) FROM books WHERE status = 'AVAILABLE'
UNION ALL SELECT 'Active Loans', COUNT(*) FROM loans WHERE status IN ('BORROWED', 'OVERDUE')
-- ... additional metrics
```

## Integration with Java Backend

### JPA Entity Mapping
The schema is designed to work seamlessly with the existing Java entities:

- `User` class maps to `users` table with inheritance for `Member`, `Librarian`, `Guest`
- `Book` class maps to `books` table with `@ManyToOne` relationships
- `Loan` class maps to `loans` table with proper foreign key mappings
- All enums (BookStatus, UserRole, etc.) map to VARCHAR fields with CHECK constraints

### Hibernate Configuration
```properties
# Enable SQL logging for development
quarkus.hibernate-orm.log.sql=true

# Database generation strategy
quarkus.hibernate-orm.database.generation=validate

# For production, use validate instead of drop-and-create
```

## Backup and Recovery

### Backup Strategy
1. **Daily Full Backups**: Complete database backup every night
2. **Transaction Log Backups**: Every 15 minutes during business hours
3. **Schema Versioning**: Track all schema changes with migration scripts

### Recovery Procedures
1. **Point-in-Time Recovery**: Restore to any point within retention period
2. **Table-Level Recovery**: Restore individual tables if needed
3. **Data Validation**: Verify data integrity after any recovery operation

---

## Quick Reference

### Key Tables and Their Purpose
| Table | Purpose | Key Relationships |
|-------|---------|------------------|
| users | Central user management | → addresses, memberships, loans, reservations, fines |
| books | Book inventory | authors ←, categories ←, → loans, reservations |
| loans | Borrowing transactions | users ←, books ←, → fines |
| reservations | Book reservations | users ←, books ← |
| fines | Financial penalties | users ←, loans ← |

### Important Status Values
- **Book Status**: AVAILABLE, BORROWED, MAINTENANCE, RESERVED
- **Loan Status**: BORROWED, RETURNED, OVERDUE, LOST
- **User Roles**: GUEST, MEMBER, LIBRARIAN
- **Membership Types**: STANDARD, PREMIUM, STUDENT

---

*This documentation should be updated whenever schema changes are made.* 