import { Book, BookCreateInput, BookUpdateInput } from '@/lib/types/book';
import { v4 as uuidv4 } from 'uuid';

const BOOKS_KEY = 'davon_library_books';

// Initialize with some example books if none exist
const initializeBooks = () => {
    if (typeof window === 'undefined') return;
    
    const books = localStorage.getItem(BOOKS_KEY);
    if (!books) {
        const defaultBooks: Book[] = [
            {
                id: uuidv4(),
                title: 'The Great Gatsby',
                author: 'F. Scott Fitzgerald',
                isbn: '978-0743273565',
                category: 'Fiction',
                description: 'The story of the mysteriously wealthy Jay Gatsby and his love for the beautiful Daisy Buchanan.',
                publishDate: '1925-04-10',
                status: 'available',
                createdAt: new Date().toISOString(),
                updatedAt: new Date().toISOString(),
            },
            // Add more default books as needed
        ];
        localStorage.setItem(BOOKS_KEY, JSON.stringify(defaultBooks));
    }
};

class BookService {
    getAllBooks = (): Book[] => {
        if (typeof window === 'undefined') return [];
        
        initializeBooks();
        const books = localStorage.getItem(BOOKS_KEY);
        return books ? JSON.parse(books) : [];
    };

    getBookById = (id: string): Book | null => {
        if (typeof window === 'undefined') return null;
        
        const books = this.getAllBooks();
        return books.find(book => book.id === id) || null;
    };

    createBook = (input: BookCreateInput): Book => {
        if (typeof window === 'undefined') 
            throw new Error('Cannot create book on server side');
        
        const books = this.getAllBooks();
        const newBook: Book = {
            id: uuidv4(),
            ...input,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
        };
        
        books.push(newBook);
        localStorage.setItem(BOOKS_KEY, JSON.stringify(books));
        return newBook;
    };

    updateBook = (id: string, input: BookUpdateInput): Book => {
        if (typeof window === 'undefined') 
            throw new Error('Cannot update book on server side');
        
        const books = this.getAllBooks();
        const bookIndex = books.findIndex(book => book.id === id);
        
        if (bookIndex === -1) {
            throw new Error('Book not found');
        }
        
        const updatedBook = {
            ...books[bookIndex],
            ...input,
            updatedAt: new Date().toISOString(),
        };
        
        books[bookIndex] = updatedBook;
        localStorage.setItem(BOOKS_KEY, JSON.stringify(books));
        return updatedBook;
    };

    deleteBook = (id: string): void => {
        if (typeof window === 'undefined') 
            throw new Error('Cannot delete book on server side');
        
        const books = this.getAllBooks();
        const filteredBooks = books.filter(book => book.id !== id);
        localStorage.setItem(BOOKS_KEY, JSON.stringify(filteredBooks));
    };

    // Additional methods for book management
    getBooksByCategory = (category: string): Book[] => {
        const books = this.getAllBooks();
        return books.filter(book => book.category === category);
    };

    getBooksByStatus = (status: Book['status']): Book[] => {
        const books = this.getAllBooks();
        return books.filter(book => book.status === status);
    };

    searchBooks = (query: string): Book[] => {
        const books = this.getAllBooks();
        const lowercaseQuery = query.toLowerCase();
        return books.filter(book => 
            book.title.toLowerCase().includes(lowercaseQuery) ||
            book.author.toLowerCase().includes(lowercaseQuery) ||
            book.isbn.includes(query)
        );
    };
}

export const bookService = new BookService(); 