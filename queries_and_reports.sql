-- =====================================================
-- DAVON LIBRARY MANAGEMENT SYSTEM - QUERIES & REPORTS
-- =====================================================
-- Author: Library Management Team
-- Created: 2024
-- Description: Common queries and reports for library operations (MSSQL Compatible)
-- =====================================================

-- =====================================================
-- BOOK SEARCH QUERIES
-- =====================================================

-- 1. Search books by title (partial match)
SELECT 
    b.id,
    b.title,
    a.first_name + ' ' + a.last_name as author_name,
    c.name as category,
    b.isbn,
    b.status,
    b.publish_date
FROM books b
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN categories c ON b.category_id = c.id
WHERE b.title LIKE '%Harry%'
ORDER BY b.title;

-- 2. Search books by author name
SELECT 
    b.id,
    b.title,
    a.first_name + ' ' + a.last_name as author_name,
    c.name as category,
    b.isbn,
    b.status
FROM books b
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN categories c ON b.category_id = c.id
WHERE a.last_name LIKE '%King%' OR a.first_name LIKE '%Stephen%'
ORDER BY a.last_name, a.first_name, b.title;

-- 3. Search books by category
SELECT 
    b.id,
    b.title,
    a.first_name + ' ' + a.last_name as author_name,
    b.isbn,
    b.status,
    b.publish_date
FROM books b
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN categories c ON b.category_id = c.id
WHERE c.name = 'Fiction'
ORDER BY b.title;

-- 4. Search available books only
SELECT 
    b.id,
    b.title,
    a.first_name + ' ' + a.last_name as author_name,
    c.name as category,
    b.isbn
FROM books b
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN categories c ON b.category_id = c.id
WHERE b.status = 'AVAILABLE'
ORDER BY b.title;

-- 5. Advanced search with multiple criteria
SELECT 
    b.id,
    b.title,
    a.first_name + ' ' + a.last_name as author_name,
    c.name as category,
    b.isbn,
    b.status,
    b.publish_date
FROM books b
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN categories c ON b.category_id = c.id
WHERE (b.title LIKE '%Pride%' OR b.description LIKE '%romance%')
    AND c.name = 'Fiction'
    AND b.status = 'AVAILABLE'
ORDER BY b.title;

-- =====================================================
-- USER MANAGEMENT QUERIES
-- =====================================================

-- 6. Get user details with membership information
SELECT 
    u.id,
    u.username,
    u.first_name + ' ' + u.last_name as full_name,
    u.email,
    u.phone_number,
    u.role,
    m.type as membership_type,
    m.status as membership_status,
    m.start_date,
    m.end_date
FROM users u
LEFT JOIN memberships m ON u.id = m.user_id AND m.status = 'ACTIVE'
WHERE u.role = 'MEMBER'
ORDER BY u.last_name, u.first_name;

-- 7. Get user loan history
SELECT 
    u.first_name + ' ' + u.last_name as user_name,
    b.title,
    a.first_name + ' ' + a.last_name as author_name,
    l.borrow_date,
    l.due_date,
    l.return_date,
    l.status,
    CASE 
        WHEN l.return_date IS NULL AND l.due_date < CAST(GETDATE() AS DATE) THEN 'OVERDUE'
        WHEN l.return_date IS NULL THEN 'ACTIVE'
        WHEN l.return_date > l.due_date THEN 'LATE RETURN'
        ELSE 'ON TIME'
    END as return_status
FROM loans l
INNER JOIN users u ON l.user_id = u.id
INNER JOIN books b ON l.book_id = b.id
INNER JOIN authors a ON b.author_id = a.id
WHERE u.username = 'alice_wonder'
ORDER BY l.borrow_date DESC;

-- 8. Get users with active loans
SELECT 
    u.id,
    u.first_name + ' ' + u.last_name as user_name,
    u.email,
    COUNT(l.id) as active_loans
FROM users u
INNER JOIN loans l ON u.id = l.user_id
WHERE l.status IN ('BORROWED', 'OVERDUE')
GROUP BY u.id, u.first_name, u.last_name, u.email
ORDER BY active_loans DESC, u.last_name;

-- =====================================================
-- LOAN MANAGEMENT QUERIES
-- =====================================================

-- 9. Current active loans with details
SELECT 
    l.id as loan_id,
    u.first_name + ' ' + u.last_name as borrower_name,
    u.email,
    u.phone_number,
    b.title as book_title,
    a.first_name + ' ' + a.last_name as author_name,
    l.borrow_date,
    l.due_date,
    DATEDIFF(day, l.due_date, GETDATE()) as days_past_due,
    l.status
FROM loans l
INNER JOIN users u ON l.user_id = u.id
INNER JOIN books b ON l.book_id = b.id
INNER JOIN authors a ON b.author_id = a.id
WHERE l.status IN ('BORROWED', 'OVERDUE')
ORDER BY l.due_date;

-- 10. Overdue books report
SELECT 
    l.id as loan_id,
    u.first_name + ' ' + u.last_name as borrower_name,
    u.email,
    u.phone_number,
    b.title as book_title,
    b.isbn,
    l.borrow_date,
    l.due_date,
    DATEDIFF(day, l.due_date, GETDATE()) as days_overdue,
    CASE 
        WHEN DATEDIFF(day, l.due_date, GETDATE()) <= 7 THEN 'MILD'
        WHEN DATEDIFF(day, l.due_date, GETDATE()) <= 30 THEN 'MODERATE'
        ELSE 'SEVERE'
    END as overdue_severity
FROM loans l
INNER JOIN users u ON l.user_id = u.id
INNER JOIN books b ON l.book_id = b.id
WHERE l.due_date < CAST(GETDATE() AS DATE) AND l.status = 'BORROWED'
ORDER BY days_overdue DESC;

-- 11. Books due soon (next 7 days)
SELECT 
    l.id as loan_id,
    u.first_name + ' ' + u.last_name as borrower_name,
    u.email,
    b.title as book_title,
    l.borrow_date,
    l.due_date,
    DATEDIFF(day, GETDATE(), l.due_date) as days_until_due
FROM loans l
INNER JOIN users u ON l.user_id = u.id
INNER JOIN books b ON l.book_id = b.id
WHERE l.status = 'BORROWED'
    AND l.due_date BETWEEN CAST(GETDATE() AS DATE) AND DATEADD(day, 7, GETDATE())
ORDER BY l.due_date;

-- =====================================================
-- INVENTORY AND STATISTICS REPORTS
-- =====================================================

-- 12. Books by status summary
SELECT 
    status,
    COUNT(*) as book_count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM books), 2) as percentage
FROM books
GROUP BY status
ORDER BY book_count DESC;

-- 13. Books by category
SELECT 
    c.name as category,
    COUNT(b.id) as total_books,
    SUM(CASE WHEN b.status = 'AVAILABLE' THEN 1 ELSE 0 END) as available_books,
    SUM(CASE WHEN b.status = 'BORROWED' THEN 1 ELSE 0 END) as borrowed_books
FROM categories c
LEFT JOIN books b ON c.id = b.category_id
GROUP BY c.id, c.name
ORDER BY total_books DESC;

-- 14. Most popular books (most borrowed)
SELECT TOP 10
    b.title,
    a.first_name + ' ' + a.last_name as author_name,
    c.name as category,
    COUNT(l.id) as total_loans,
    COUNT(CASE WHEN l.status = 'RETURNED' THEN 1 END) as completed_loans,
    COUNT(CASE WHEN l.status IN ('BORROWED', 'OVERDUE') THEN 1 END) as active_loans
FROM books b
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN categories c ON b.category_id = c.id
LEFT JOIN loans l ON b.id = l.book_id
GROUP BY b.id, b.title, a.first_name, a.last_name, c.name
HAVING COUNT(l.id) > 0
ORDER BY total_loans DESC, completed_loans DESC;

-- 15. Most active borrowers
SELECT TOP 10
    u.first_name + ' ' + u.last_name as user_name,
    u.email,
    m.type as membership_type,
    COUNT(l.id) as total_loans,
    COUNT(CASE WHEN l.status = 'RETURNED' THEN 1 END) as returned_loans,
    COUNT(CASE WHEN l.status IN ('BORROWED', 'OVERDUE') THEN 1 END) as active_loans,
    COUNT(CASE WHEN l.return_date > l.due_date THEN 1 END) as late_returns
FROM users u
LEFT JOIN memberships m ON u.id = m.user_id AND m.status = 'ACTIVE'
LEFT JOIN loans l ON u.id = l.user_id
WHERE u.role = 'MEMBER'
GROUP BY u.id, u.first_name, u.last_name, u.email, m.type
HAVING COUNT(l.id) > 0
ORDER BY total_loans DESC;

-- =====================================================
-- RESERVATION QUERIES
-- =====================================================

-- 16. Active reservations
SELECT 
    r.id as reservation_id,
    u.first_name + ' ' + u.last_name as user_name,
    u.email,
    b.title as book_title,
    a.first_name + ' ' + a.last_name as author_name,
    r.reservation_date,
    r.expiration_date,
    DATEDIFF(day, GETDATE(), r.expiration_date) as days_until_expiration
FROM reservations r
INNER JOIN users u ON r.user_id = u.id
INNER JOIN books b ON r.book_id = b.id
INNER JOIN authors a ON b.author_id = a.id
WHERE r.status = 'ACTIVE'
ORDER BY r.expiration_date;

-- 17. Reservations expiring soon
SELECT 
    r.id as reservation_id,
    u.first_name + ' ' + u.last_name as user_name,
    u.email,
    u.phone_number,
    b.title as book_title,
    r.reservation_date,
    r.expiration_date
FROM reservations r
INNER JOIN users u ON r.user_id = u.id
INNER JOIN books b ON r.book_id = b.id
WHERE r.status = 'ACTIVE'
    AND r.expiration_date BETWEEN CAST(GETDATE() AS DATE) AND DATEADD(day, 3, GETDATE())
ORDER BY r.expiration_date;

-- =====================================================
-- FINANCIAL REPORTS (FINES)
-- =====================================================

-- 18. Outstanding fines report
SELECT 
    f.id as fine_id,
    u.first_name + ' ' + u.last_name as user_name,
    u.email,
    u.phone_number,
    b.title as book_title,
    f.amount,
    f.reason,
    f.issued_date,
    DATEDIFF(day, f.issued_date, GETDATE()) as days_outstanding
FROM fines f
INNER JOIN users u ON f.user_id = u.id
INNER JOIN loans l ON f.loan_id = l.id
INNER JOIN books b ON l.book_id = b.id
WHERE f.status = 'UNPAID'
ORDER BY f.issued_date, f.amount DESC;

-- 19. Fine collection summary
SELECT 
    FORMAT(f.issued_date, 'yyyy-MM') as month,
    COUNT(*) as total_fines,
    SUM(f.amount) as total_amount,
    SUM(CASE WHEN f.status = 'PAID' THEN f.amount ELSE 0 END) as collected_amount,
    SUM(CASE WHEN f.status = 'UNPAID' THEN f.amount ELSE 0 END) as outstanding_amount,
    SUM(CASE WHEN f.status = 'WAIVED' THEN f.amount ELSE 0 END) as waived_amount
FROM fines f
WHERE f.issued_date >= DATEADD(month, -12, GETDATE())
GROUP BY FORMAT(f.issued_date, 'yyyy-MM')
ORDER BY month DESC;

-- 20. Users with highest fines
SELECT 
    u.first_name + ' ' + u.last_name as user_name,
    u.email,
    COUNT(f.id) as total_fines,
    SUM(f.amount) as total_fine_amount,
    SUM(CASE WHEN f.status = 'UNPAID' THEN f.amount ELSE 0 END) as outstanding_amount
FROM users u
INNER JOIN fines f ON u.id = f.user_id
GROUP BY u.id, u.first_name, u.last_name, u.email
HAVING SUM(f.amount) > 0
ORDER BY outstanding_amount DESC, total_fine_amount DESC;

-- =====================================================
-- OPERATIONAL REPORTS
-- =====================================================

-- 21. Daily activity summary
SELECT 
    CAST(GETDATE() AS DATE) as report_date,
    (SELECT COUNT(*) FROM loans WHERE borrow_date = CAST(GETDATE() AS DATE)) as books_borrowed_today,
    (SELECT COUNT(*) FROM loans WHERE return_date = CAST(GETDATE() AS DATE)) as books_returned_today,
    (SELECT COUNT(*) FROM reservations WHERE reservation_date = CAST(GETDATE() AS DATE)) as reservations_made_today,
    (SELECT COUNT(*) FROM users WHERE CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)) as new_users_today;

-- 22. Monthly statistics
SELECT 
    FORMAT(GETDATE(), 'yyyy-MM') as month,
    (SELECT COUNT(*) FROM loans WHERE FORMAT(borrow_date, 'yyyy-MM') = FORMAT(GETDATE(), 'yyyy-MM')) as monthly_loans,
    (SELECT COUNT(*) FROM loans WHERE FORMAT(return_date, 'yyyy-MM') = FORMAT(GETDATE(), 'yyyy-MM')) as monthly_returns,
    (SELECT COUNT(*) FROM reservations WHERE FORMAT(reservation_date, 'yyyy-MM') = FORMAT(GETDATE(), 'yyyy-MM')) as monthly_reservations,
    (SELECT COUNT(*) FROM users WHERE FORMAT(created_at, 'yyyy-MM') = FORMAT(GETDATE(), 'yyyy-MM')) as new_users;

-- 23. Membership statistics
SELECT 
    m.type as membership_type,
    COUNT(*) as total_members,
    SUM(CASE WHEN m.status = 'ACTIVE' THEN 1 ELSE 0 END) as active_members,
    SUM(CASE WHEN m.status = 'EXPIRED' THEN 1 ELSE 0 END) as expired_members,
    SUM(CASE WHEN m.status = 'SUSPENDED' THEN 1 ELSE 0 END) as suspended_members
FROM memberships m
GROUP BY m.type
ORDER BY total_members DESC;

-- =====================================================
-- MAINTENANCE AND ADMINISTRATION QUERIES
-- =====================================================

-- 24. Books that need maintenance
SELECT 
    b.id,
    b.title,
    a.first_name + ' ' + a.last_name as author_name,
    b.isbn,
    b.status,
    b.updated_at as last_updated
FROM books b
INNER JOIN authors a ON b.author_id = a.id
WHERE b.status = 'MAINTENANCE'
ORDER BY b.updated_at;

-- 25. Users with expired memberships
SELECT 
    u.id,
    u.first_name + ' ' + u.last_name as user_name,
    u.email,
    u.phone_number,
    m.type as membership_type,
    m.end_date,
    DATEDIFF(day, m.end_date, GETDATE()) as days_expired
FROM users u
INNER JOIN memberships m ON u.id = m.user_id
WHERE m.end_date < CAST(GETDATE() AS DATE) AND m.status = 'ACTIVE'
ORDER BY m.end_date;

-- 26. Database health check
SELECT 'Total Users' as metric, COUNT(*) as count FROM users
UNION ALL
SELECT 'Total Books', COUNT(*) FROM books
UNION ALL
SELECT 'Available Books', COUNT(*) FROM books WHERE status = 'AVAILABLE'
UNION ALL
SELECT 'Active Loans', COUNT(*) FROM loans WHERE status IN ('BORROWED', 'OVERDUE')
UNION ALL
SELECT 'Overdue Loans', COUNT(*) FROM loans WHERE status = 'OVERDUE' OR (status = 'BORROWED' AND due_date < CAST(GETDATE() AS DATE))
UNION ALL
SELECT 'Active Reservations', COUNT(*) FROM reservations WHERE status = 'ACTIVE'
UNION ALL
SELECT 'Unpaid Fines', COUNT(*) FROM fines WHERE status = 'UNPAID'
UNION ALL
SELECT 'Active Memberships', COUNT(*) FROM memberships WHERE status = 'ACTIVE';

-- =====================================================
-- QUERIES AND REPORTS COLLECTION COMPLETE
-- ===================================================== 