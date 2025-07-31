import { httpClient } from '../client';
import { API_CONFIG } from '../config';
import { Author } from '../types';

export interface AuthorCreateRequest {
  firstName: string;
  lastName: string;
  biography?: string;
  birthDate?: string;
  nationality?: string;
}

export interface AuthorUpdateRequest {
  firstName?: string;
  lastName?: string;
  biography?: string;
  birthDate?: string;
  nationality?: string;
}

export class AuthorService {
  /**
   * Get all authors
   */
  async getAllAuthors(): Promise<Author[]> {
    try {
      return await httpClient.get<Author[]>(API_CONFIG.ENDPOINTS.AUTHORS.BASE);
    } catch (error) {
      console.error('Failed to fetch authors:', error);
      throw error;
    }
  }

  /**
   * Get author by ID
   */
  async getAuthorById(id: number): Promise<Author> {
    try {
      return await httpClient.get<Author>(API_CONFIG.ENDPOINTS.AUTHORS.BY_ID(id));
    } catch (error) {
      console.error(`Failed to fetch author with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Create new author
   */
  async createAuthor(authorData: AuthorCreateRequest): Promise<Author> {
    try {
      return await httpClient.post<Author>(
        API_CONFIG.ENDPOINTS.AUTHORS.BASE,
        authorData
      );
    } catch (error) {
      console.error('Failed to create author:', error);
      throw error;
    }
  }

  /**
   * Update existing author
   */
  async updateAuthor(id: number, authorData: AuthorUpdateRequest): Promise<Author> {
    try {
      return await httpClient.put<Author>(
        API_CONFIG.ENDPOINTS.AUTHORS.BY_ID(id),
        authorData
      );
    } catch (error) {
      console.error(`Failed to update author with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Delete author
   */
  async deleteAuthor(id: number): Promise<void> {
    try {
      await httpClient.delete<void>(API_CONFIG.ENDPOINTS.AUTHORS.BY_ID(id));
    } catch (error) {
      console.error(`Failed to delete author with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Search authors by name (using backend search endpoint)
   */
  async searchAuthorsByName(name: string): Promise<Author[]> {
    try {
      if (!name || name.trim().length === 0) {
        return [];
      }

      const params = { name: name.trim() };
      return await httpClient.get<Author[]>(`${API_CONFIG.ENDPOINTS.AUTHORS.BASE}/search`, params);
    } catch (error) {
      console.error('Failed to search authors:', error);
      throw error;
    }
  }

  /**
   * Get authors for dropdown/select components
   */
  async getAuthorsForDropdown(): Promise<{ id: number; label: string; value: number }[]> {
    try {
      const authors = await this.getAllAuthors();
      return authors.map(author => ({
        id: author.id,
        label: `${author.firstName} ${author.lastName}`,
        value: author.id
      }));
    } catch (error) {
      console.error('Failed to fetch authors for dropdown:', error);
      return [];
    }
  }

  /**
   * Get author suggestions for autocomplete
   */
  async getAuthorSuggestions(partial: string, maxResults: number = 10): Promise<Author[]> {
    try {
      if (!partial || partial.trim().length < 2) {
        return [];
      }

      const authors = await this.searchAuthorsByName(partial);
      return authors.slice(0, maxResults);
    } catch (error) {
      console.error('Failed to get author suggestions:', error);
      return [];
    }
  }

  /**
   * Check if author already exists by name
   */
  async doesAuthorExist(firstName: string, lastName: string, excludeId?: number): Promise<boolean> {
    try {
      const authors = await this.getAllAuthors();
      return authors.some(author => 
        author.firstName.toLowerCase() === firstName.toLowerCase() &&
        author.lastName.toLowerCase() === lastName.toLowerCase() &&
        author.id !== excludeId
      );
    } catch (error) {
      console.error('Failed to check if author exists:', error);
      return false;
    }
  }

  /**
   * Get authors with book count (requires additional API call)
   */
  async getAuthorsWithBookCount(): Promise<(Author & { bookCount: number })[]> {
    try {
      // This would require a backend endpoint that includes book counts
      // For now, we'll return authors with bookCount as 0
      const authors = await this.getAllAuthors();
      return authors.map(author => ({
        ...author,
        bookCount: 0 // TODO: Implement backend endpoint for book counts
      }));
    } catch (error) {
      console.error('Failed to fetch authors with book count:', error);
      throw error;
    }
  }

  /**
   * Get full author name
   */
  getFullName(author: Author): string {
    return `${author.firstName} ${author.lastName}`;
  }
}

// Create and export singleton instance
export const authorService = new AuthorService(); 