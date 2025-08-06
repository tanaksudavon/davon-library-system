-- Create favorites table for user favorite books functionality (MSSQL version)

-- Check if table exists, if not create it
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='favorites' AND xtype='U')
BEGIN
    CREATE TABLE favorites (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id BIGINT NOT NULL,
        book_id BIGINT NOT NULL,
        created_at DATETIME2 DEFAULT GETDATE(),
        
        -- Foreign key constraints
        CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        CONSTRAINT fk_favorite_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
        
        -- Unique constraint to prevent duplicate favorites
        CONSTRAINT unique_user_book_favorite UNIQUE (user_id, book_id)
    );
    
    -- Create indexes for faster queries
    CREATE INDEX idx_favorites_user_id ON favorites(user_id);
    CREATE INDEX idx_favorites_book_id ON favorites(book_id);
    
    PRINT 'Favorites table created successfully';
END
ELSE
BEGIN
    PRINT 'Favorites table already exists';
END

-- Insert some sample data if tables exist and have data
IF EXISTS (SELECT 1 FROM users WHERE user_type = 'MEMBER') AND EXISTS (SELECT 1 FROM books)
BEGIN
    INSERT INTO favorites (user_id, book_id) 
    SELECT TOP 10 u.id, b.id 
    FROM users u 
    CROSS JOIN books b 
    WHERE u.user_type = 'MEMBER' 
    AND b.id IN (1, 2, 3)
    AND NOT EXISTS (
        SELECT 1 FROM favorites f 
        WHERE f.user_id = u.id AND f.book_id = b.id
    );
    
    PRINT 'Sample favorites data inserted';
END