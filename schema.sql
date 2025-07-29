-- =====================================================
-- LIBRARY MANAGEMENT SYSTEM DATABASE SCHEMA
-- Compatible with: H2, PostgreSQL, MySQL, MS SQL Server
-- Version: 2.0
-- =====================================================

-- Drop existing tables (in correct order due to foreign key constraints)
IF OBJECT_ID('fines', 'U') IS NOT NULL DROP TABLE fines;
IF OBJECT_ID('reservations', 'U') IS NOT NULL DROP TABLE reservations;
IF OBJECT_ID('loans', 'U') IS NOT NULL DROP TABLE loans;
IF OBJECT_ID('memberships', 'U') IS NOT NULL DROP TABLE memberships;
IF OBJECT_ID('addresses', 'U') IS NOT NULL DROP TABLE addresses;
IF OBJECT_ID('books', 'U') IS NOT NULL DROP TABLE books;
IF OBJECT_ID('categories', 'U') IS NOT NULL DROP TABLE categories;
IF OBJECT_ID('authors', 'U') IS NOT NULL DROP TABLE authors;
IF OBJECT_ID('users', 'U') IS NOT NULL DROP TABLE users;
GO

-- Drop existing views
IF OBJECT_ID('v_available_books', 'V') IS NOT NULL DROP VIEW v_available_books;
IF OBJECT_ID('v_current_loans', 'V') IS NOT NULL DROP VIEW v_current_loans;
IF OBJECT_ID('v_overdue_books', 'V') IS NOT NULL DROP VIEW v_overdue_books;
IF OBJECT_ID('v_user_loan_history', 'V') IS NOT NULL DROP VIEW v_user_loan_history;
IF OBJECT_ID('v_popular_books', 'V') IS NOT NULL DROP VIEW v_popular_books;
GO

-- Drop existing triggers
IF OBJECT_ID('trg_loan_book_status', 'TR') IS NOT NULL DROP TRIGGER trg_loan_book_status;
IF OBJECT_ID('trg_return_book_status', 'TR') IS NOT NULL DROP TRIGGER trg_return_book_status;
IF OBJECT_ID('trg_overdue_fine', 'TR') IS NOT NULL DROP TRIGGER trg_overdue_fine;
GO

-- =====================================================
-- CORE TABLES
-- =====================================================

-- Users table (base table for inheritance)
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    date_of_birth DATE,
    user_type VARCHAR(20) NOT NULL CHECK (user_type IN ('MEMBER', 'LIBRARIAN', 'GUEST')),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);
GO

-- Authors table
CREATE TABLE authors (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    biography TEXT,
    birth_date DATE,
    nationality VARCHAR(100),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);
GO

-- Categories table
CREATE TABLE categories (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);
GO

-- Books table
CREATE TABLE books (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    author_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    description TEXT,
    publish_date DATE,
    cover_image VARCHAR(500),
    status VARCHAR(20) DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'BORROWED', 'RESERVED', 'MAINTENANCE', 'LOST')),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (author_id) REFERENCES authors(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
GO

-- Addresses table (for user addresses)
CREATE TABLE addresses (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address_type VARCHAR(20) DEFAULT 'HOME' CHECK (address_type IN ('HOME', 'WORK', 'OTHER')),
    street_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state_province VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100) NOT NULL,
    is_primary BIT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
GO

-- Memberships table (for member-specific information)
CREATE TABLE memberships (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    membership_number VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(20) DEFAULT 'STANDARD' CHECK (type IN ('STANDARD', 'PREMIUM', 'STUDENT', 'SENIOR')),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'EXPIRED', 'SUSPENDED')),
    max_books_allowed INT DEFAULT 5,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
GO

-- Loans table
CREATE TABLE loans (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) DEFAULT 'BORROWED' CHECK (status IN ('BORROWED', 'RETURNED', 'OVERDUE', 'LOST')),
    renewal_count INT DEFAULT 0,
    notes TEXT,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
GO

-- Reservations table
CREATE TABLE reservations (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reservation_date DATE NOT NULL,
    expiration_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'FULFILLED', 'CANCELLED', 'EXPIRED')),
    priority_level INT DEFAULT 1,
    notes TEXT,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
GO

-- Fines table
CREATE TABLE fines (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    loan_id BIGINT,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    issued_date DATE NOT NULL,
    paid_date DATE,
    status VARCHAR(20) DEFAULT 'UNPAID' CHECK (status IN ('UNPAID', 'PAID', 'WAIVED')),
    payment_method VARCHAR(50),
    notes TEXT,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (loan_id) REFERENCES loans(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
GO

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================

-- User indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_type ON users(user_type);
CREATE INDEX idx_users_status ON users(status);
GO

-- Book indexes
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_author ON books(author_id);
CREATE INDEX idx_books_category ON books(category_id);
CREATE INDEX idx_books_status ON books(status);
GO

-- Author indexes
CREATE INDEX idx_authors_name ON authors(last_name, first_name);
GO

-- Loan indexes
CREATE INDEX idx_loans_user ON loans(user_id);
CREATE INDEX idx_loans_book ON loans(book_id);
CREATE INDEX idx_loans_dates ON loans(borrow_date, due_date);
CREATE INDEX idx_loans_status ON loans(status);
GO

-- Reservation indexes
CREATE INDEX idx_reservations_user ON reservations(user_id);
CREATE INDEX idx_reservations_book ON reservations(book_id);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_reservations_date ON reservations(reservation_date);
GO

-- Fine indexes
CREATE INDEX idx_fines_user ON fines(user_id);
CREATE INDEX idx_fines_loan ON fines(loan_id);
CREATE INDEX idx_fines_status ON fines(status);
GO

-- Address indexes
CREATE INDEX idx_addresses_user ON addresses(user_id);
GO

-- Membership indexes
CREATE INDEX idx_memberships_user ON memberships(user_id);
CREATE INDEX idx_memberships_number ON memberships(membership_number);
CREATE INDEX idx_memberships_status ON memberships(status);
CREATE INDEX idx_memberships_type ON memberships(type);
GO

-- =====================================================
-- CONSTRAINTS AND BUSINESS RULES
-- =====================================================

-- Ensure due date is after borrow date
ALTER TABLE loans ADD CONSTRAINT chk_loan_dates 
    CHECK (due_date > borrow_date);
GO

-- Ensure return date is after borrow date (when not null)
ALTER TABLE loans ADD CONSTRAINT chk_return_date 
    CHECK (return_date IS NULL OR return_date >= borrow_date);
GO

-- Ensure reservation expiration is after reservation date
ALTER TABLE reservations ADD CONSTRAINT chk_reservation_dates 
    CHECK (expiration_date > reservation_date);
GO

-- Ensure membership end date is after start date
ALTER TABLE memberships ADD CONSTRAINT chk_membership_dates 
    CHECK (end_date > start_date);
GO

-- Ensure fine amount is positive
ALTER TABLE fines ADD CONSTRAINT chk_fine_amount 
    CHECK (amount >= 0);
GO

-- Prevent self-referential issues and ensure data integrity
ALTER TABLE books ADD CONSTRAINT chk_positive_ids 
    CHECK (author_id > 0 AND category_id > 0);
GO

-- =====================================================
-- TRIGGERS FOR AUTOMATIC UPDATES (MSSQL SYNTAX)
-- =====================================================

-- Trigger to update book status when loaned
CREATE TRIGGER trg_loan_book_status
ON loans
AFTER INSERT
AS
BEGIN
    UPDATE books 
    SET status = 'BORROWED' 
    WHERE id IN (SELECT book_id FROM inserted);
END;
GO

-- Trigger to update book status when returned
CREATE TRIGGER trg_return_book_status
ON loans
AFTER UPDATE
AS
BEGIN
    IF UPDATE(status)
    BEGIN
        UPDATE books 
        SET status = 'AVAILABLE' 
        WHERE id IN (
            SELECT i.book_id 
            FROM inserted i 
            INNER JOIN deleted d ON i.id = d.id 
            WHERE i.status = 'RETURNED' AND d.status != 'RETURNED'
        );
    END
END;
GO

-- Trigger to automatically create fine for overdue books
CREATE TRIGGER trg_overdue_fine
ON loans
AFTER UPDATE
AS
BEGIN
    IF UPDATE(status)
    BEGIN
        INSERT INTO fines (loan_id, user_id, amount, reason, issued_date)
        SELECT i.id, i.user_id, 5.00, 'Overdue book fine', CAST(GETDATE() AS DATE)
        FROM inserted i 
        INNER JOIN deleted d ON i.id = d.id 
        WHERE i.status = 'OVERDUE' AND d.status != 'OVERDUE';
    END
END;
GO

-- =====================================================
-- VIEWS FOR COMMON QUERIES (MSSQL SYNTAX)
-- =====================================================

-- View for available books with author and category information
CREATE VIEW v_available_books AS
SELECT 
    b.id,
    b.title,
    b.isbn,
    b.description,
    b.publish_date,
    a.first_name + ' ' + a.last_name as author_name,
    c.name as category_name,
    b.status
FROM books b
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN categories c ON b.category_id = c.id
WHERE b.status = 'AVAILABLE';
GO

-- View for current loans with user and book information
CREATE VIEW v_current_loans AS
SELECT 
    l.id as loan_id,
    u.first_name + ' ' + u.last_name as borrower_name,
    u.email as borrower_email,
    b.title as book_title,
    b.isbn,
    l.borrow_date,
    l.due_date,
    l.status,
    CASE 
        WHEN l.due_date < CAST(GETDATE() AS DATE) AND l.status = 'BORROWED' THEN 'OVERDUE'
        ELSE l.status
    END as current_status
FROM loans l
INNER JOIN users u ON l.user_id = u.id
INNER JOIN books b ON l.book_id = b.id
WHERE l.status IN ('BORROWED', 'OVERDUE');
GO

-- View for overdue books
CREATE VIEW v_overdue_books AS
SELECT 
    l.id as loan_id,
    u.first_name + ' ' + u.last_name as borrower_name,
    u.email as borrower_email,
    u.phone_number,
    b.title as book_title,
    l.borrow_date,
    l.due_date,
    DATEDIFF(day, l.due_date, GETDATE()) as days_overdue
FROM loans l
INNER JOIN users u ON l.user_id = u.id
INNER JOIN books b ON l.book_id = b.id
WHERE l.due_date < CAST(GETDATE() AS DATE) AND l.status = 'BORROWED';
GO

-- View for user loan history
CREATE VIEW v_user_loan_history AS
SELECT 
    u.id as user_id,
    u.first_name + ' ' + u.last_name as user_name,
    b.title as book_title,
    a.first_name + ' ' + a.last_name as author_name,
    l.borrow_date,
    l.due_date,
    l.return_date,
    l.status
FROM loans l
INNER JOIN users u ON l.user_id = u.id
INNER JOIN books b ON l.book_id = b.id
INNER JOIN authors a ON b.author_id = a.id;
GO

-- View for popular books (most borrowed)
CREATE VIEW v_popular_books AS
SELECT 
    b.id,
    b.title,
    a.first_name + ' ' + a.last_name as author_name,
    c.name as category_name,
    COUNT(l.id) as total_loans,
    AVG(CAST(DATEDIFF(day, l.borrow_date, ISNULL(l.return_date, GETDATE())) AS FLOAT)) as avg_loan_duration
FROM books b
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN categories c ON b.category_id = c.id
LEFT JOIN loans l ON b.id = l.book_id
GROUP BY b.id, b.title, a.first_name, a.last_name, c.name;
GO

-- =====================================================
-- INITIAL SETUP COMPLETE
-- =====================================================
-- Schema creation completed successfully
-- Ready for sample data insertion
-- =====================================================
