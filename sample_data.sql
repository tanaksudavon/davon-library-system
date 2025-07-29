-- =====================================================
-- LIBRARY MANAGEMENT SYSTEM - SAMPLE DATA
-- Compatible with: H2, PostgreSQL, MySQL, MS SQL Server
-- Version: 2.0
-- =====================================================

-- =====================================================
-- INSERT AUTHORS
-- =====================================================

INSERT INTO authors (first_name, last_name, biography, birth_date, nationality) VALUES ('Jane', 'Austen', 'English novelist known for her wit and social commentary', '1775-12-16', 'British');
INSERT INTO authors (first_name, last_name, biography, birth_date, nationality) VALUES ('J.K.', 'Rowling', 'British author, creator of the Harry Potter series', '1965-07-31', 'British');
INSERT INTO authors (first_name, last_name, biography, birth_date, nationality) VALUES ('Stephen', 'King', 'American author of horror, supernatural fiction, and fantasy', '1947-09-21', 'American');
INSERT INTO authors (first_name, last_name, biography, birth_date, nationality) VALUES ('Agatha', 'Christie', 'British crime novelist, creator of Hercule Poirot and Miss Marple', '1890-09-15', 'British');
INSERT INTO authors (first_name, last_name, biography, birth_date, nationality) VALUES ('Isaac', 'Asimov', 'American writer and professor, known for science fiction', '1920-01-02', 'American');
INSERT INTO authors (first_name, last_name, biography, birth_date, nationality) VALUES ('George', 'Orwell', 'English novelist and essayist, known for dystopian fiction', '1903-06-25', 'British');
INSERT INTO authors (first_name, last_name, biography, birth_date, nationality) VALUES ('Harper', 'Lee', 'American novelist, author of To Kill a Mockingbird', '1926-04-28', 'American');
INSERT INTO authors (first_name, last_name, biography, birth_date, nationality) VALUES ('Mark', 'Twain', 'American writer and humorist', '1835-11-30', 'American');
INSERT INTO authors (first_name, last_name, biography, birth_date, nationality) VALUES ('William', 'Shakespeare', 'English playwright and poet', '1564-04-23', 'British');
INSERT INTO authors (first_name, last_name, biography, birth_date, nationality) VALUES ('Charles', 'Dickens', 'English writer and social critic', '1812-02-07', 'British');

-- =====================================================
-- INSERT CATEGORIES
-- =====================================================

INSERT INTO categories (name, description) VALUES ('Fiction', 'Literary works of imaginative narration');
INSERT INTO categories (name, description) VALUES ('Non-Fiction', 'Factual and informative works');
INSERT INTO categories (name, description) VALUES ('Biography', 'Life stories of notable individuals');
INSERT INTO categories (name, description) VALUES ('Mystery', 'Detective and mystery novels');
INSERT INTO categories (name, description) VALUES ('Fantasy', 'Fantasy and magical realism');
INSERT INTO categories (name, description) VALUES ('Science Fiction', 'Futuristic and speculative fiction');
INSERT INTO categories (name, description) VALUES ('Romance', 'Love stories and romantic fiction');
INSERT INTO categories (name, description) VALUES ('History', 'Historical accounts and documentation');
INSERT INTO categories (name, description) VALUES ('Self-Help', 'Personal development and improvement');
INSERT INTO categories (name, description) VALUES ('Children', 'Books for young readers');

-- =====================================================
-- INSERT USERS
-- =====================================================

-- Librarians
INSERT INTO users (first_name, last_name, email, phone_number, date_of_birth, user_type, status) VALUES ('John', 'Smith', 'admin@library.com', '555-0101', '1980-05-15', 'LIBRARIAN', 'ACTIVE');
INSERT INTO users (first_name, last_name, email, phone_number, date_of_birth, user_type, status) VALUES ('Sarah', 'Jones', 'sarah.jones@library.com', '555-0102', '1985-08-22', 'LIBRARIAN', 'ACTIVE');
INSERT INTO users (first_name, last_name, email, phone_number, date_of_birth, user_type, status) VALUES ('Michael', 'Brown', 'michael.brown@library.com', '555-0103', '1978-12-10', 'LIBRARIAN', 'ACTIVE');

-- Members
INSERT INTO users (first_name, last_name, email, phone_number, date_of_birth, user_type, status) VALUES ('Alice', 'Wonder', 'alice.wonder@email.com', '555-0201', '1992-03-14', 'MEMBER', 'ACTIVE');
INSERT INTO users (first_name, last_name, email, phone_number, date_of_birth, user_type, status) VALUES ('Bob', 'Builder', 'bob.builder@email.com', '555-0202', '1988-07-08', 'MEMBER', 'ACTIVE');
INSERT INTO users (first_name, last_name, email, phone_number, date_of_birth, user_type, status) VALUES ('Carol', 'Singer', 'carol.singer@email.com', '555-0203', '1995-11-25', 'MEMBER', 'ACTIVE');
INSERT INTO users (first_name, last_name, email, phone_number, date_of_birth, user_type, status) VALUES ('David', 'Writer', 'david.writer@email.com', '555-0204', '1987-01-30', 'MEMBER', 'ACTIVE');
INSERT INTO users (first_name, last_name, email, phone_number, date_of_birth, user_type, status) VALUES ('Emma', 'Reader', 'emma.reader@email.com', '555-0205', '1993-09-18', 'MEMBER', 'ACTIVE');
INSERT INTO users (first_name, last_name, email, phone_number, date_of_birth, user_type, status) VALUES ('Frank', 'Student', 'frank.student@university.edu', '555-0206', '2000-06-12', 'MEMBER', 'ACTIVE');
INSERT INTO users (first_name, last_name, email, phone_number, date_of_birth, user_type, status) VALUES ('Grace', 'Teacher', 'grace.teacher@school.edu', '555-0207', '1982-04-07', 'MEMBER', 'ACTIVE');
INSERT INTO users (first_name, last_name, email, phone_number, date_of_birth, user_type, status) VALUES ('Henry', 'Doctor', 'henry.doctor@hospital.com', '555-0208', '1975-10-20', 'MEMBER', 'ACTIVE');
INSERT INTO users (first_name, last_name, email, phone_number, date_of_birth, user_type, status) VALUES ('Isabella', 'Artist', 'isabella.artist@gallery.com', '555-0209', '1990-02-14', 'MEMBER', 'ACTIVE');
INSERT INTO users (first_name, last_name, email, phone_number, date_of_birth, user_type, status) VALUES ('Jack', 'Engineer', 'jack.engineer@tech.com', '555-0210', '1986-12-03', 'MEMBER', 'ACTIVE');

-- Guests
INSERT INTO users (first_name, last_name, email, phone_number, date_of_birth, user_type, status) VALUES ('Guest', 'User1', 'guest1@temp.com', '555-0301', '1985-01-01', 'GUEST', 'ACTIVE');
INSERT INTO users (first_name, last_name, email, phone_number, date_of_birth, user_type, status) VALUES ('Guest', 'User2', 'guest2@temp.com', '555-0302', '1990-01-01', 'GUEST', 'ACTIVE');

-- =====================================================
-- INSERT BOOKS
-- =====================================================

INSERT INTO books (title, isbn, author_id, category_id, description, publish_date, status) VALUES ('Pride and Prejudice', '978-0-14-143951-8', 1, 1, 'A romantic novel about Elizabeth Bennet and Mr. Darcy', '1813-01-28', 'AVAILABLE');
INSERT INTO books (title, isbn, author_id, category_id, description, publish_date, status) VALUES ('Harry Potter and the Philosopher''s Stone', '978-0-74-753100-3', 2, 5, 'A young wizard discovers his magical heritage', '1997-06-26', 'BORROWED');
INSERT INTO books (title, isbn, author_id, category_id, description, publish_date, status) VALUES ('Harry Potter and the Chamber of Secrets', '978-0-74-754612-0', 2, 5, 'Harry''s second year at Hogwarts', '1998-07-02', 'AVAILABLE');
INSERT INTO books (title, isbn, author_id, category_id, description, publish_date, status) VALUES ('The Shining', '978-0-38-512179-9', 3, 1, 'A family becomes caretakers of an isolated hotel', '1977-01-28', 'AVAILABLE');
INSERT INTO books (title, isbn, author_id, category_id, description, publish_date, status) VALUES ('Murder on the Orient Express', '978-0-06-207350-4', 4, 4, 'Hercule Poirot investigates a murder on a train', '1934-01-01', 'BORROWED');
INSERT INTO books (title, isbn, author_id, category_id, description, publish_date, status) VALUES ('Foundation', '978-0-55-329312-6', 5, 6, 'The first book in the Foundation science fiction series', '1951-05-01', 'AVAILABLE');
INSERT INTO books (title, isbn, author_id, category_id, description, publish_date, status) VALUES ('1984', '978-0-45-228423-4', 6, 1, 'A dystopian social science fiction novel', '1949-06-08', 'AVAILABLE');
INSERT INTO books (title, isbn, author_id, category_id, description, publish_date, status) VALUES ('To Kill a Mockingbird', '978-0-06-112008-4', 7, 1, 'A story of racial injustice and childhood in the American South', '1960-07-11', 'RESERVED');
INSERT INTO books (title, isbn, author_id, category_id, description, publish_date, status) VALUES ('The Adventures of Tom Sawyer', '978-0-14-062755-8', 8, 1, 'The adventures of a young boy growing up along the Mississippi River', '1876-01-01', 'AVAILABLE');
INSERT INTO books (title, isbn, author_id, category_id, description, publish_date, status) VALUES ('Romeo and Juliet', '978-0-74-347174-7', 9, 1, 'A tragedy about two young star-crossed lovers', '1597-01-01', 'AVAILABLE');
INSERT INTO books (title, isbn, author_id, category_id, description, publish_date, status) VALUES ('A Christmas Carol', '978-0-14-062765-7', 10, 1, 'A Christmas ghost story of redemption', '1843-12-19', 'AVAILABLE');
INSERT INTO books (title, isbn, author_id, category_id, description, publish_date, status) VALUES ('It', '978-0-45-141043-4', 3, 1, 'A horror novel about a creature that preys on children', '1986-09-15', 'MAINTENANCE');
INSERT INTO books (title, isbn, author_id, category_id, description, publish_date, status) VALUES ('The Hobbit', '978-0-54-792822-7', 2, 5, 'A fantasy adventure about Bilbo Baggins', '1937-09-21', 'AVAILABLE');
INSERT INTO books (title, isbn, author_id, category_id, description, publish_date, status) VALUES ('Animal Farm', '978-0-45-228424-1', 6, 1, 'An allegorical novella about farm animals', '1945-08-17', 'AVAILABLE');
INSERT INTO books (title, isbn, author_id, category_id, description, publish_date, status) VALUES ('Sense and Sensibility', '978-0-74-327356-5', 1, 7, 'Jane Austen''s first published novel', '1811-10-30', 'AVAILABLE');

-- =====================================================
-- INSERT ADDRESSES
-- =====================================================

INSERT INTO addresses (user_id, address_type, street_address, city, state_province, postal_code, country, is_primary) VALUES (4, 'HOME', '123 Wonder Street', 'Wonderland', 'NY', '10001', 'USA', 1);
INSERT INTO addresses (user_id, address_type, street_address, city, state_province, postal_code, country, is_primary) VALUES (5, 'HOME', '456 Builder Avenue', 'Construction City', 'CA', '90210', 'USA', 1);
INSERT INTO addresses (user_id, address_type, street_address, city, state_province, postal_code, country, is_primary) VALUES (6, 'HOME', '789 Melody Lane', 'Music Town', 'TN', '37201', 'USA', 1);
INSERT INTO addresses (user_id, address_type, street_address, city, state_province, postal_code, country, is_primary) VALUES (7, 'HOME', '321 Writer''s Block', 'Story City', 'IL', '60601', 'USA', 1);
INSERT INTO addresses (user_id, address_type, street_address, city, state_province, postal_code, country, is_primary) VALUES (8, 'HOME', '654 Reading Road', 'Book Borough', 'MA', '02101', 'USA', 1);
INSERT INTO addresses (user_id, address_type, street_address, city, state_province, postal_code, country, is_primary) VALUES (9, 'HOME', '987 Campus Drive', 'University Heights', 'OH', '44106', 'USA', 1);
INSERT INTO addresses (user_id, address_type, street_address, city, state_province, postal_code, country, is_primary) VALUES (10, 'HOME', '147 School Street', 'Education Valley', 'TX', '75201', 'USA', 1);
INSERT INTO addresses (user_id, address_type, street_address, city, state_province, postal_code, country, is_primary) VALUES (11, 'HOME', '258 Hospital Way', 'Medical Center', 'FL', '33101', 'USA', 1);
INSERT INTO addresses (user_id, address_type, street_address, city, state_province, postal_code, country, is_primary) VALUES (12, 'HOME', '369 Art District', 'Creative Commons', 'OR', '97201', 'USA', 1);
INSERT INTO addresses (user_id, address_type, street_address, city, state_province, postal_code, country, is_primary) VALUES (13, 'HOME', '741 Tech Boulevard', 'Silicon Valley', 'CA', '94101', 'USA', 1);

-- =====================================================
-- INSERT MEMBERSHIPS
-- =====================================================

INSERT INTO memberships (user_id, membership_number, type, start_date, end_date, status, max_books_allowed) VALUES (4, 'MEM-2024-001', 'STANDARD', '2024-01-15', '2025-01-15', 'ACTIVE', 5);
INSERT INTO memberships (user_id, membership_number, type, start_date, end_date, status, max_books_allowed) VALUES (5, 'MEM-2024-002', 'PREMIUM', '2024-02-01', '2025-02-01', 'ACTIVE', 10);
INSERT INTO memberships (user_id, membership_number, type, start_date, end_date, status, max_books_allowed) VALUES (6, 'MEM-2024-003', 'STANDARD', '2024-01-20', '2025-01-20', 'ACTIVE', 5);
INSERT INTO memberships (user_id, membership_number, type, start_date, end_date, status, max_books_allowed) VALUES (7, 'MEM-2024-004', 'STUDENT', '2024-09-01', '2025-09-01', 'ACTIVE', 7);
INSERT INTO memberships (user_id, membership_number, type, start_date, end_date, status, max_books_allowed) VALUES (8, 'MEM-2024-005', 'STANDARD', '2024-03-10', '2025-03-10', 'ACTIVE', 5);
INSERT INTO memberships (user_id, membership_number, type, start_date, end_date, status, max_books_allowed) VALUES (9, 'MEM-2024-006', 'STUDENT', '2024-08-15', '2025-08-15', 'ACTIVE', 7);
INSERT INTO memberships (user_id, membership_number, type, start_date, end_date, status, max_books_allowed) VALUES (10, 'MEM-2024-007', 'PREMIUM', '2024-01-05', '2025-01-05', 'ACTIVE', 10);
INSERT INTO memberships (user_id, membership_number, type, start_date, end_date, status, max_books_allowed) VALUES (11, 'MEM-2024-008', 'SENIOR', '2024-04-01', '2025-04-01', 'ACTIVE', 8);
INSERT INTO memberships (user_id, membership_number, type, start_date, end_date, status, max_books_allowed) VALUES (12, 'MEM-2024-009', 'STANDARD', '2024-02-14', '2025-02-14', 'ACTIVE', 5);
INSERT INTO memberships (user_id, membership_number, type, start_date, end_date, status, max_books_allowed) VALUES (13, 'MEM-2024-010', 'PREMIUM', '2024-01-30', '2025-01-30', 'ACTIVE', 10);

-- =====================================================
-- INSERT LOANS
-- =====================================================

INSERT INTO loans (book_id, user_id, borrow_date, due_date, return_date, status, renewal_count, notes) VALUES (2, 4, '2024-11-01', '2024-11-15', NULL, 'BORROWED', 0, 'First loan for Alice');
INSERT INTO loans (book_id, user_id, borrow_date, due_date, return_date, status, renewal_count, notes) VALUES (5, 5, '2024-10-25', '2024-11-08', NULL, 'OVERDUE', 1, 'Renewed once, now overdue');
INSERT INTO loans (book_id, user_id, borrow_date, due_date, return_date, status, renewal_count, notes) VALUES (8, 6, '2024-11-10', '2024-11-24', NULL, 'BORROWED', 0, 'Reserved book loan');
INSERT INTO loans (book_id, user_id, borrow_date, due_date, return_date, status, renewal_count, notes) VALUES (1, 7, '2024-10-15', '2024-10-29', '2024-10-28', 'RETURNED', 0, 'Returned early');
INSERT INTO loans (book_id, user_id, borrow_date, due_date, return_date, status, renewal_count, notes) VALUES (3, 8, '2024-11-05', '2024-11-19', NULL, 'BORROWED', 0, 'Standard loan');
INSERT INTO loans (book_id, user_id, borrow_date, due_date, return_date, status, renewal_count, notes) VALUES (6, 9, '2024-10-20', '2024-11-03', '2024-11-02', 'RETURNED', 0, 'Completed loan');
INSERT INTO loans (book_id, user_id, borrow_date, due_date, return_date, status, renewal_count, notes) VALUES (7, 10, '2024-11-08', '2024-11-22', NULL, 'BORROWED', 0, 'Premium member loan');
INSERT INTO loans (book_id, user_id, borrow_date, due_date, return_date, status, renewal_count, notes) VALUES (9, 11, '2024-10-30', '2024-11-13', NULL, 'BORROWED', 0, 'Senior member loan');
INSERT INTO loans (book_id, user_id, borrow_date, due_date, return_date, status, renewal_count, notes) VALUES (10, 12, '2024-11-12', '2024-11-26', NULL, 'BORROWED', 0, 'Artist member loan');
INSERT INTO loans (book_id, user_id, borrow_date, due_date, return_date, status, renewal_count, notes) VALUES (11, 13, '2024-11-01', '2024-11-15', '2024-11-14', 'RETURNED', 0, 'Engineer member loan');

-- =====================================================
-- INSERT RESERVATIONS
-- =====================================================

INSERT INTO reservations (book_id, user_id, reservation_date, expiration_date, status, priority_level, notes) VALUES (8, 4, '2024-11-05', '2024-11-12', 'FULFILLED', 1, 'Reserved and fulfilled');
INSERT INTO reservations (book_id, user_id, reservation_date, expiration_date, status, priority_level, notes) VALUES (2, 6, '2024-11-15', '2024-11-22', 'ACTIVE', 1, 'Waiting for Harry Potter');
INSERT INTO reservations (book_id, user_id, reservation_date, expiration_date, status, priority_level, notes) VALUES (5, 7, '2024-11-10', '2024-11-17', 'ACTIVE', 2, 'Second in queue for mystery book');
INSERT INTO reservations (book_id, user_id, reservation_date, expiration_date, status, priority_level, notes) VALUES (12, 8, '2024-11-08', '2024-11-15', 'CANCELLED', 1, 'User cancelled reservation');
INSERT INTO reservations (book_id, user_id, reservation_date, expiration_date, status, priority_level, notes) VALUES (1, 9, '2024-10-25', '2024-11-01', 'EXPIRED', 1, 'Reservation expired');
INSERT INTO reservations (book_id, user_id, reservation_date, expiration_date, status, priority_level, notes) VALUES (14, 10, '2024-11-12', '2024-11-19', 'ACTIVE', 1, 'Premium member priority');
INSERT INTO reservations (book_id, user_id, reservation_date, expiration_date, status, priority_level, notes) VALUES (1, 11, '2024-11-14', '2024-11-21', 'ACTIVE', 1, 'Senior member reservation');
INSERT INTO reservations (book_id, user_id, reservation_date, expiration_date, status, priority_level, notes) VALUES (13, 12, '2024-11-16', '2024-11-23', 'ACTIVE', 1, 'Artist wants fantasy book');

-- =====================================================
-- INSERT FINES
-- =====================================================

INSERT INTO fines (loan_id, user_id, amount, reason, issued_date, paid_date, status, payment_method, notes) VALUES (2, 5, 15.00, 'Overdue book fine - 15 days late', '2024-11-09', NULL, 'UNPAID', NULL, 'Murder on the Orient Express overdue');
INSERT INTO fines (loan_id, user_id, amount, reason, issued_date, paid_date, status, payment_method, notes) VALUES (6, 9, 5.00, 'Late return fee', '2024-11-04', '2024-11-05', 'PAID', 'CASH', 'Paid at library counter');
INSERT INTO fines (loan_id, user_id, amount, reason, issued_date, paid_date, status, payment_method, notes) VALUES (NULL, 4, 10.00, 'Lost library card replacement', '2024-10-20', '2024-10-20', 'PAID', 'CARD', 'New card issued');
INSERT INTO fines (loan_id, user_id, amount, reason, issued_date, paid_date, status, payment_method, notes) VALUES (NULL, 7, 25.00, 'Damaged book replacement fee', '2024-11-01', NULL, 'UNPAID', NULL, 'Water damage to book cover');
INSERT INTO fines (loan_id, user_id, amount, reason, issued_date, paid_date, status, payment_method, notes) VALUES (4, 13, 2.50, 'Processing fee for renewal', '2024-11-02', '2024-11-02', 'PAID', 'ONLINE', 'Automatic payment processed');

-- =====================================================
-- DATA INSERTION COMPLETE
-- =====================================================
-- Sample data has been successfully inserted
-- Database is ready for testing and development
-- ===================================================== 