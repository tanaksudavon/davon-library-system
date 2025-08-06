-- Add missing book_title column to notifications table
USE LibraryDB;

-- Check if the column doesn't exist and add it
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'notifications' AND COLUMN_NAME = 'book_title')
BEGIN
    ALTER TABLE notifications ADD book_title NVARCHAR(255);
    PRINT 'Added book_title column to notifications table';
END
ELSE
BEGIN
    PRINT 'book_title column already exists in notifications table';
END 