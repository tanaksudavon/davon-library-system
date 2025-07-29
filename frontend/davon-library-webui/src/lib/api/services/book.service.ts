import { httpClient } from '../client';
import { API_CONFIG } from '../config';
import { 
  Book, 
  BookCreateRequest, 
  BookUpdateRequest, 
  BookSearchFilters,
  PaginatedResponse 
} from '../types';

export class BookService {
  /**
   * Get all books
   */
  async getAllBooks(): Promise<Book[]> {
    try {
      return await httpClient.get<Book[]>(API_CONFIG.ENDPOINTS.BOOKS.BASE);
    } catch (error) {
      console.error('Failed to fetch books:', error);
      throw error;
    }
  }

  /**
   * Get book by ID
   */
  async getBookById(id: number): Promise<Book> {
    try {
      return await httpClient.get<Book>(API_CONFIG.ENDPOINTS.BOOKS.BY_ID(id));
    } catch (error) {
      console.error(`Failed to fetch book with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Create new book
   */
  async createBook(bookData: BookCreateRequest): Promise<Book> {
    try {
      // Transform the request to match backend expectations
      const requestData = {
        title: bookData.title,
        isbn: bookData.isbn,
        description: bookData.description,
        publishDate: bookData.publishDate,
        coverImage: bookData.coverImage,
        status: bookData.status,
        author: { id: bookData.authorId }, // Backend expects nested object
        category: { id: bookData.categoryId }, // Backend expects nested object
      };

      return await httpClient.post<Book>(
        API_CONFIG.ENDPOINTS.BOOKS.BASE,
        requestData
      );
    } catch (error) {
      console.error('Failed to create book:', error);
      throw error;
    }
  }

  /**
   * Update existing book
   */
  async updateBook(id: number, bookData: BookUpdateRequest): Promise<Book> {
    try {
      // Transform the request to match backend expectations
      const requestData = {
        id,
        title: bookData.title,
        isbn: bookData.isbn,
        description: bookData.description,
        publishDate: bookData.publishDate,
        coverImage: bookData.coverImage,
        status: bookData.status,
        ...(bookData.authorId && { author: { id: bookData.authorId } }),
        ...(bookData.categoryId && { category: { id: bookData.categoryId } }),
      };

      return await httpClient.put<Book>(
        API_CONFIG.ENDPOINTS.BOOKS.BY_ID(id),
        requestData
      );
    } catch (error) {
      console.error(`Failed to update book with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Delete book
   */
  async deleteBook(id: number): Promise<void> {
    try {
      await httpClient.delete<void>(API_CONFIG.ENDPOINTS.BOOKS.BY_ID(id));
    } catch (error) {
      console.error(`Failed to delete book with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Search books with filters
   */
  async searchBooks(filters: BookSearchFilters): Promise<Book[]> {
    try {
      // Build query parameters
      const params: Record<string, string> = {};
      
      if (filters.title) params.title = filters.title;
      if (filters.author) params.author = filters.author;
      if (filters.category) params.category = filters.category;
      if (filters.isbn) params.isbn = filters.isbn;
      if (filters.status) params.status = filters.status;
      if (filters.publishedAfter) params.publishedAfter = filters.publishedAfter;
      if (filters.publishedBefore) params.publishedBefore = filters.publishedBefore;

      return await httpClient.get<Book[]>(
        API_CONFIG.ENDPOINTS.SEARCH.BOOKS,
        params
      );
    } catch (error) {
      console.error('Failed to search books:', error);
      throw error;
    }
  }

  /**
   * Get books with pagination
   */
  async getBooksWithPagination(
    page: number = 1,
    limit: number = 12,
    filters?: BookSearchFilters
  ): Promise<PaginatedResponse<Book>> {
    try {
      const params: Record<string, string> = {
        page: page.toString(),
        limit: limit.toString(),
      };

      // Add filters if provided
      if (filters) {
        if (filters.title) params.title = filters.title;
        if (filters.author) params.author = filters.author;
        if (filters.category) params.category = filters.category;
        if (filters.isbn) params.isbn = filters.isbn;
        if (filters.status) params.status = filters.status;
        if (filters.publishedAfter) params.publishedAfter = filters.publishedAfter;
        if (filters.publishedBefore) params.publishedBefore = filters.publishedBefore;
      }

      // For now, simulate pagination on the frontend since backend might not support it yet
      const allBooks = await this.getAllBooks();
      
      // Apply filters if provided
      let filteredBooks = allBooks;
      if (filters) {
        filteredBooks = this.applyFilters(allBooks, filters);
      }

      // Apply pagination
      const startIndex = (page - 1) * limit;
      const endIndex = startIndex + limit;
      const paginatedBooks = filteredBooks.slice(startIndex, endIndex);

      return {
        data: paginatedBooks,
        currentPage: page,
        totalPages: Math.ceil(filteredBooks.length / limit),
        totalItems: filteredBooks.length,
        itemsPerPage: limit,
      };
    } catch (error) {
      console.error('Failed to fetch paginated books:', error);
      throw error;
    }
  }

  /**
   * Apply filters to books array (client-side filtering)
   */
  private applyFilters(books: Book[], filters: BookSearchFilters): Book[] {
    return books.filter(book => {
      // Title filter
      if (filters.title && !book.title.toLowerCase().includes(filters.title.toLowerCase())) {
        return false;
      }

      // Author filter
      if (filters.author) {
        const authorName = `${book.author.firstName} ${book.author.lastName}`.toLowerCase();
        if (!authorName.includes(filters.author.toLowerCase())) {
          return false;
        }
      }

      // Category filter
      if (filters.category && book.category.name !== filters.category) {
        return false;
      }

      // ISBN filter
      if (filters.isbn && !book.isbn.includes(filters.isbn)) {
        return false;
      }

      // Status filter
      if (filters.status && book.status !== filters.status) {
        return false;
      }

      // Date filters
      if (filters.publishedAfter && book.publishDate) {
        if (new Date(book.publishDate) < new Date(filters.publishedAfter)) {
          return false;
        }
      }

      if (filters.publishedBefore && book.publishDate) {
        if (new Date(book.publishDate) > new Date(filters.publishedBefore)) {
          return false;
        }
      }

      return true;
    });
  }

  /**
   * Get books by category
   */
  async getBooksByCategory(categoryId: number): Promise<Book[]> {
    try {
      const allBooks = await this.getAllBooks();
      return allBooks.filter(book => book.category.id === categoryId);
    } catch (error) {
      console.error(`Failed to fetch books for category ${categoryId}:`, error);
      throw error;
    }
  }

  /**
   * Get books by author
   */
  async getBooksByAuthor(authorId: number): Promise<Book[]> {
    try {
      const allBooks = await this.getAllBooks();
      return allBooks.filter(book => book.author.id === authorId);
    } catch (error) {
      console.error(`Failed to fetch books for author ${authorId}:`, error);
      throw error;
    }
  }

  /**
   * Get available books only
   */
  async getAvailableBooks(): Promise<Book[]> {
    try {
      const allBooks = await this.getAllBooks();
      return allBooks.filter(book => book.status === 'AVAILABLE');
    } catch (error) {
      console.error('Failed to fetch available books:', error);
      throw error;
    }
  }

  /**
   * Check book availability
   */
  async isBookAvailable(bookId: number): Promise<boolean> {
    try {
      const book = await this.getBookById(bookId);
      return book.status === 'AVAILABLE';
    } catch (error) {
      console.error(`Failed to check availability for book ${bookId}:`, error);
      return false;
    }
  }
}

// Create and export singleton instance
export const bookService = new BookService(); 