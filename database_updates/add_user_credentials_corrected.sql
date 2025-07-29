-- Update remaining users with usernames and passwords
-- This script adds login credentials to users who currently have null username/password

-- Update remaining Librarians (admin users)
UPDATE users SET username = 'sarah.jones', password = 'librarian123' WHERE id = 2;
UPDATE users SET username = 'michael.brown', password = 'librarian123' WHERE id = 3;

-- Update Members with usernames and passwords
UPDATE users SET username = 'alice.wonder', password = 'member123' WHERE id = 4;
UPDATE users SET username = 'bob.builder', password = 'member123' WHERE id = 5;
UPDATE users SET username = 'carol.singer', password = 'member123' WHERE id = 6;
UPDATE users SET username = 'david.writer', password = 'member123' WHERE id = 7;
UPDATE users SET username = 'emma.reader', password = 'member123' WHERE id = 8;
UPDATE users SET username = 'frank.student', password = 'student123' WHERE id = 9;
UPDATE users SET username = 'grace.teacher', password = 'member123' WHERE id = 10;
UPDATE users SET username = 'henry.doctor', password = 'member123' WHERE id = 11;
UPDATE users SET username = 'isabella.artist', password = 'member123' WHERE id = 12;
UPDATE users SET username = 'jack.engineer', password = 'member123' WHERE id = 13;

-- Update Guest users
UPDATE users SET username = 'guest1', password = 'guest123' WHERE id = 14;
UPDATE users SET username = 'guest2', password = 'guest123' WHERE id = 15;

-- Verify all updates
SELECT id, username, email, first_name, last_name, user_type, status 
FROM users 
WHERE username IS NOT NULL 
ORDER BY id; 