-- Update existing users with usernames and passwords
-- This script adds login credentials to users who currently have null username/password
-- Run this script against your LibraryDB database

-- Update Librarians (admin users)
UPDATE users 
SET username = 'admin', password = 'admin123' 
WHERE id = 1 AND email = 'admin@library.com';

UPDATE users 
SET username = 'sarah.jones', password = 'librarian123' 
WHERE id = 2 AND email = 'sarah.jones@library.com';

UPDATE users 
SET username = 'michael.brown', password = 'librarian123' 
WHERE id = 3 AND email = 'michael.brown@library.com';

-- Update Members with usernames based on their emails and standard password
UPDATE users 
SET username = 'alice.wonder', password = 'member123' 
WHERE id = 4 AND email = 'alice.wonder@email.com';

UPDATE users 
SET username = 'bob.builder', password = 'member123' 
WHERE id = 5 AND email = 'bob.builder@email.com';

UPDATE users 
SET username = 'carol.singer', password = 'member123' 
WHERE id = 6 AND email = 'carol.singer@email.com';

UPDATE users 
SET username = 'david.writer', password = 'member123' 
WHERE id = 7 AND email = 'david.writer@email.com';

UPDATE users 
SET username = 'emma.reader', password = 'member123' 
WHERE id = 8 AND email = 'emma.reader@email.com';

UPDATE users 
SET username = 'frank.student', password = 'student123' 
WHERE id = 9 AND email = 'frank.student@university.edu';

UPDATE users 
SET username = 'grace.teacher', password = 'member123' 
WHERE id = 10 AND email = 'grace.teacher@school.edu';

UPDATE users 
SET username = 'henry.doctor', password = 'member123' 
WHERE id = 11 AND email = 'henry.doctor@hospital.com';

UPDATE users 
SET username = 'isabella.artist', password = 'member123' 
WHERE id = 12 AND email = 'isabella.artist@gallery.com';

UPDATE users 
SET username = 'jack.engineer', password = 'member123' 
WHERE id = 13 AND email = 'jack.engineer@tech.com';

-- Update Guest users
UPDATE users 
SET username = 'guest1', password = 'guest123' 
WHERE id = 14 AND email = 'guest1@temp.com';

UPDATE users 
SET username = 'guest2', password = 'guest123' 
WHERE id = 15 AND email = 'guest2@temp.com';

-- Verify the updates
SELECT id, username, email, firstName, lastName, role, status 
FROM users 
WHERE username IS NOT NULL 
ORDER BY id; 