-- Fix active reservations that should be fulfilled
-- Update reservations to FULFILLED status when user has an active loan for the same book

UPDATE reservation 
SET status = 'FULFILLED', 
    updated_at = CURRENT_TIMESTAMP
WHERE status = 'ACTIVE' 
AND id IN (
    SELECT r.id 
    FROM reservation r
    INNER JOIN loan l ON r.book_id = l.book_id AND r.user_id = l.user_id
    WHERE r.status = 'ACTIVE' 
    AND l.return_date IS NULL  -- Active loan (not returned)
    AND l.status = 'BORROWED'
);

-- Show updated reservations
SELECT 
    r.id as reservation_id,
    u.first_name || ' ' || u.last_name as user_name,
    b.title as book_title,
    r.status as reservation_status,
    l.id as loan_id,
    l.borrow_date,
    l.status as loan_status
FROM reservation r
JOIN users u ON r.user_id = u.id
JOIN book b ON r.book_id = b.id
LEFT JOIN loan l ON r.book_id = l.book_id AND r.user_id = l.user_id AND l.return_date IS NULL
WHERE r.status = 'FULFILLED'
ORDER BY r.updated_at DESC;