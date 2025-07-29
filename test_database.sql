-- =====================================================
-- DATABASE VALIDATION TEST SCRIPT
-- =====================================================
-- This script tests the database schema functionality
-- Run after schema.sql and sample_data.sql
-- =====================================================

-- Test 1: Verify all tables exist and have data
SELECT 'TABLE EXISTENCE TEST' as test_name;
SELECT 
    table_name,
    (SELECT COUNT(*) FROM information_schema.columns WHERE table_name = t.table_name) as column_count
FROM information_schema.tables t
WHERE table_schema = 'PUBLIC'  -- H2 specific, use 'public' for PostgreSQL
    AND table_type = 'BASE TABLE'
    AND table_name IN ('USERS', 'AUTHORS', 'CATEGORIES', 'BOOKS', 'ADDRESSES', 
                       'MEMBERSHIPS', 'LOANS', 'RESERVATIONS', 'FINES')
ORDER BY table_name;

-- Test 2: Verify data was inserted correctly
SELECT 'DATA INSERTION TEST' as test_name;
SELECT 'Users' as entity, COUNT(*) as count FROM users
UNION ALL SELECT 'Authors', COUNT(*) FROM authors
UNION ALL SELECT 'Categories', COUNT(*) FROM categories
UNION ALL SELECT 'Books', COUNT(*) FROM books
UNION ALL SELECT 'Addresses', COUNT(*) FROM addresses
UNION ALL SELECT 'Memberships', COUNT(*) FROM memberships
UNION ALL SELECT 'Loans', COUNT(*) FROM loans
UNION ALL SELECT 'Reservations', COUNT(*) FROM reservations
UNION ALL SELECT 'Fines', COUNT(*) FROM fines;

-- Test 3: Verify foreign key relationships
SELECT 'FOREIGN KEY TEST' as test_name;
-- Test books have valid authors and categories
SELECT 
    'Books with valid references' as test,
    COUNT(*) as count
FROM books b
JOIN authors a ON b.author_id = a.id
JOIN categories c ON b.category_id = c.id;

-- Test loans have valid users and books
SELECT 
    'Loans with valid references' as test,
    COUNT(*) as count
FROM loans l
JOIN users u ON l.user_id = u.id
JOIN books b ON l.book_id = b.id;

-- Test 4: Verify constraints work
SELECT 'CONSTRAINT TEST' as test_name;

-- Test unique constraints (this should return 0 duplicates)
SELECT 'Username uniqueness' as test, COUNT(*) - COUNT(DISTINCT username) as duplicates FROM users;
SELECT 'Email uniqueness' as test, COUNT(*) - COUNT(DISTINCT email) as duplicates FROM users;
SELECT 'ISBN uniqueness' as test, COUNT(*) - COUNT(DISTINCT isbn) as duplicates FROM books WHERE isbn IS NOT NULL;

-- Test check constraints (should return 0 violations)
SELECT 'Date constraint violations' as test, COUNT(*) as violations
FROM loans 
WHERE due_date <= borrow_date;

SELECT 'Return date violations' as test, COUNT(*) as violations
FROM loans 
WHERE return_date IS NOT NULL AND return_date < borrow_date;

-- Test 5: Verify indexes exist (H2 specific query)
SELECT 'INDEX TEST' as test_name;
SELECT 
    index_name,
    table_name,
    column_name
FROM information_schema.indexes
WHERE table_name IN ('USERS', 'BOOKS', 'LOANS', 'AUTHORS')
    AND index_name LIKE 'IDX_%'
ORDER BY table_name, index_name;

-- Test 6: Test views work correctly
SELECT 'VIEW TEST' as test_name;

-- Test available books view
SELECT 'Available books view' as test, COUNT(*) as count 
FROM v_available_books;

-- Test current loans view
SELECT 'Current loans view' as test, COUNT(*) as count 
FROM v_current_loans;

-- Test overdue books view
SELECT 'Overdue books view' as test, COUNT(*) as count 
FROM v_overdue_books;

-- Test 7: Verify business logic queries work
SELECT 'BUSINESS LOGIC TEST' as test_name;

-- Test book search functionality
SELECT 'Book search by title' as test, COUNT(*) as results
FROM books b
JOIN authors a ON b.author_id = a.id
JOIN categories c ON b.category_id = c.id
WHERE b.title LIKE '%Harry%';

-- Test user loan history
SELECT 'User loan history' as test, COUNT(*) as loans
FROM loans l
JOIN users u ON l.user_id = u.id
WHERE u.username = 'alice_wonder';

-- Test overdue book detection
SELECT 'Overdue detection' as test, COUNT(*) as overdue_count
FROM loans
WHERE due_date < CURRENT_DATE AND status = 'BORROWED';

-- Test 8: Verify enum values are correct
SELECT 'ENUM VALIDATION TEST' as test_name;

-- Check user roles
SELECT 'User roles' as test, role, COUNT(*) as count
FROM users
GROUP BY role
ORDER BY role;

-- Check book statuses
SELECT 'Book statuses' as test, status, COUNT(*) as count
FROM books
GROUP BY status
ORDER BY status;

-- Check loan statuses
SELECT 'Loan statuses' as test, status, COUNT(*) as count
FROM loans
GROUP BY status
ORDER BY status;

-- Test 9: Performance test on common queries
SELECT 'PERFORMANCE TEST' as test_name;

-- Test query execution (timing will vary by database)
SELECT 'Complex join performance' as test, COUNT(*) as result_count
FROM books b
JOIN authors a ON b.author_id = a.id
JOIN categories c ON b.category_id = c.id
LEFT JOIN loans l ON b.id = l.book_id
LEFT JOIN users u ON l.user_id = u.id;

-- Test 10: Verify calculated fields work
SELECT 'CALCULATED FIELDS TEST' as test_name;

-- Test days overdue calculation
SELECT 
    'Days overdue calculation' as test,
    l.id as loan_id,
    l.due_date,
    CURRENT_DATE as current_date,
    DATEDIFF(CURRENT_DATE, l.due_date) as days_overdue
FROM loans l
WHERE l.due_date < CURRENT_DATE AND l.status = 'BORROWED'
LIMIT 3;

-- Test 11: Data integrity verification
SELECT 'DATA INTEGRITY TEST' as test_name;

-- Verify no orphaned records
SELECT 'Orphaned books (no author)' as test, COUNT(*) as count
FROM books b
LEFT JOIN authors a ON b.author_id = a.id
WHERE a.id IS NULL;

SELECT 'Orphaned books (no category)' as test, COUNT(*) as count
FROM books b
LEFT JOIN categories c ON b.category_id = c.id
WHERE c.id IS NULL;

SELECT 'Orphaned loans (no user)' as test, COUNT(*) as count
FROM loans l
LEFT JOIN users u ON l.user_id = u.id
WHERE u.id IS NULL;

SELECT 'Orphaned loans (no book)' as test, COUNT(*) as count
FROM loans l
LEFT JOIN books b ON l.book_id = b.id
WHERE b.id IS NULL;

-- Test 12: Test database functions and expressions
SELECT 'DATABASE FUNCTIONS TEST' as test_name;

-- Test string concatenation
SELECT 'String concatenation' as test, 
       CONCAT(first_name, ' ', last_name) as full_name
FROM users
WHERE role = 'MEMBER'
LIMIT 3;

-- Test date arithmetic
SELECT 'Date arithmetic' as test,
       borrow_date,
       due_date,
       DATEDIFF(due_date, borrow_date) as loan_period_days
FROM loans
WHERE status = 'BORROWED'
LIMIT 3;

-- Final Summary
SELECT 'TEST SUMMARY' as test_name;
SELECT 
    'Database validation completed' as message,
    CURRENT_TIMESTAMP as test_completion_time;

-- Show potential issues (if any)
SELECT 'POTENTIAL ISSUES' as test_name;

-- Check for any constraint violations that might have been missed
SELECT 'Books without authors' as issue, COUNT(*) as count
FROM books WHERE author_id IS NULL
UNION ALL
SELECT 'Books without categories', COUNT(*) FROM books WHERE category_id IS NULL
UNION ALL  
SELECT 'Loans without users', COUNT(*) FROM loans WHERE user_id IS NULL
UNION ALL
SELECT 'Loans without books', COUNT(*) FROM loans WHERE book_id IS NULL
UNION ALL
SELECT 'Invalid loan dates', COUNT(*) FROM loans WHERE due_date <= borrow_date;

-- =====================================================
-- If all tests pass, the database is ready for use!
-- ===================================================== 