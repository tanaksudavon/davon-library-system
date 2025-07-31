import { httpClient } from '../client';
import { API_CONFIG } from '../config';
import { Book, User } from '../types';

export class SearchService {
  /**
   * Search books using server-side search
   */
  async searchBooks(searchTerm: string): Promise<Book[]> {
    try {
      if (!searchTerm || searchTerm.trim().length === 0) {
        return [];
      }

      const params = { q: searchTerm.trim() };
      return await httpClient.get<Book[]>(API_CONFIG.ENDPOINTS.SEARCH.BOOKS, params);
    } catch (error) {
      console.error('Failed to search books:', error);
      throw error;
    }
  }

  /**
   * Search users using server-side search
   */
  async searchUsers(searchTerm: string): Promise<User[]> {
    try {
      if (!searchTerm || searchTerm.trim().length === 0) {
        return [];
      }

      const params = { q: searchTerm.trim() };
      return await httpClient.get<User[]>(API_CONFIG.ENDPOINTS.SEARCH.USERS, params);
    } catch (error) {
      console.error('Failed to search users:', error);
      throw error;
    }
  }

  /**
   * Search books with debounced input for real-time search
   */
  async searchBooksDebounced(
    searchTerm: string,
    debounceMs: number = 300
  ): Promise<Book[]> {
    return new Promise((resolve) => {
      setTimeout(async () => {
        try {
          const results = await this.searchBooks(searchTerm);
          resolve(results);
        } catch (error) {
          console.error('Debounced search failed:', error);
          resolve([]);
        }
      }, debounceMs);
    });
  }

  /**
   * Global search across multiple entities (books and users)
   */
  async globalSearch(searchTerm: string): Promise<{
    books: Book[];
    users: User[];
    totalResults: number;
  }> {
    try {
      const [books, users] = await Promise.all([
        this.searchBooks(searchTerm),
        this.searchUsers(searchTerm)
      ]);

      return {
        books,
        users,
        totalResults: books.length + users.length
      };
    } catch (error) {
      console.error('Global search failed:', error);
      return {
        books: [],
        users: [],
        totalResults: 0
      };
    }
  }
}

// Create and export singleton instance
export const searchService = new SearchService(); 