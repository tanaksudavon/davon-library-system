-- Create favorites table for user favorite books functionality
CREATE TABLE IF NOT EXISTS favorites (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_favorite_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    
    -- Unique constraint to prevent duplicate favorites
    CONSTRAINT unique_user_book_favorite UNIQUE (user_id, book_id)
);

-- Create index for faster queries
CREATE INDEX IF NOT EXISTS idx_favorites_user_id ON favorites(user_id);
CREATE INDEX IF NOT EXISTS idx_favorites_book_id ON favorites(book_id);

-- Insert some sample data if tables exist and have data
INSERT INTO favorites (user_id, book_id) 
SELECT u.id, b.id 
FROM users u 
CROSS JOIN books b 
WHERE u.user_type = 'MEMBER' 
AND b.id IN (1, 2, 3)
LIMIT 10
ON CONFLICT (user_id, book_id) DO NOTHING;