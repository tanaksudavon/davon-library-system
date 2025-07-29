import { bookService as apiBookService } from '../api/services';
import { 
  Book, 
  BookCreateRequest, 
  BookUpdateRequest, 
  BookSearchFilters,
  BookStatus,
  PaginatedResponse 
} from '../api/types';

class BookService {
  /**
   * Get all books
   */
  getAllBooks(): Book[] {
    // For synchronous compatibility, we'll need to handle this differently
    // This is a temporary bridge - ideally components should use async calls
    try {
      // Return empty array for now, components should use async methods
      return [];
    } catch (error) {
      console.error('Failed to get books synchronously:', error);
      return [];
    }
  }

  /**
   * Get all books (async version)
   */
  async getAllBooksAsync(): Promise<Book[]> {
    return await apiBookService.getAllBooks();
  }

  /**
   * Get book by ID
   */
  async getBookById(id: number): Promise<Book> {
    return await apiBookService.getBookById(id);
  }

  /**
   * Create new book
   */
  async createBook(bookData: BookCreateRequest): Promise<Book> {
    return await apiBookService.createBook(bookData);
  }

  /**
   * Update existing book
   */
  async updateBook(id: number, bookData: BookUpdateRequest): Promise<Book> {
    return await apiBookService.updateBook(id, bookData);
  }

  /**
   * Delete book
   */
  async deleteBook(id: number): Promise<void> {
    return await apiBookService.deleteBook(id);
  }

  /**
   * Search books with filters
   */
  async searchBooks(filters: BookSearchFilters): Promise<Book[]> {
    return await apiBookService.searchBooks(filters);
  }

  /**
   * Get books with pagination
   */
  async getBooksWithPagination(
    page: number = 1,
    limit: number = 12,
    filters?: BookSearchFilters
  ): Promise<PaginatedResponse<Book>> {
    return await apiBookService.getBooksWithPagination(page, limit, filters);
  }

  /**
   * Get books by category
   */
  async getBooksByCategory(categoryId: number): Promise<Book[]> {
    return await apiBookService.getBooksByCategory(categoryId);
  }

  /**
   * Get books by author
   */
  async getBooksByAuthor(authorId: number): Promise<Book[]> {
    return await apiBookService.getBooksByAuthor(authorId);
  }

  /**
   * Get available books only
   */
  async getAvailableBooks(): Promise<Book[]> {
    return await apiBookService.getAvailableBooks();
  }

  /**
   * Check book availability
   */
  async isBookAvailable(bookId: number): Promise<boolean> {
    return await apiBookService.isBookAvailable(bookId);
  }

  // Legacy methods for backward compatibility
  /**
   * @deprecated Use createBook instead
   */
  createBookLegacy(bookData: any): Book {
    console.warn('createBookLegacy is deprecated, use async createBook instead');
    // Return a mock book for compatibility
    return {
      id: Date.now(),
      title: bookData.title || '',
      isbn: bookData.isbn || '',
      description: bookData.description,
      publishDate: bookData.publishDate,
      coverImage: bookData.coverImage,
      status: bookData.status || 'AVAILABLE',
      author: bookData.author || { id: 1, firstName: 'Unknown', lastName: 'Author' },
      category: bookData.category || { id: 1, name: 'General' },
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    } as Book;
  }

  /**
   * @deprecated Use updateBook instead
   */
  updateBookLegacy(bookData: any): Book {
    console.warn('updateBookLegacy is deprecated, use async updateBook instead');
    return bookData as Book;
  }

  /**
   * @deprecated Use deleteBook instead
   */
  deleteBookLegacy(id: number): void {
    console.warn('deleteBookLegacy is deprecated, use async deleteBook instead');
  }

  /**
   * Get sample book data for testing
   */
  getSampleBooks(): Book[] {
    return [
      {
        id: 1,
        title: 'The Great Gatsby',
        isbn: '978-0-7432-7356-5',
        description: 'A classic American novel',
        publishDate: '1925-04-10',
        coverImage: '/api/placeholder/300/400',
        status: BookStatus.AVAILABLE,
        author: { id: 1, firstName: 'F. Scott', lastName: 'Fitzgerald' },
        category: { id: 1, name: 'Fiction' },
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      },
      {
        id: 2,
        title: 'To Kill a Mockingbird',
        isbn: '978-0-06-112008-4',
        description: 'A gripping tale of racial injustice',
        publishDate: '1960-07-11',
        coverImage: '/api/placeholder/300/400',
        status: BookStatus.BORROWED,
        author: { id: 2, firstName: 'Harper', lastName: 'Lee' },
        category: { id: 1, name: 'Fiction' },
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      },
    ];
  }
}

export const bookService = new BookService(); 