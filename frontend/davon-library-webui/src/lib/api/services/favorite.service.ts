import { httpClient } from '../client';

export interface Favorite {
  id: number;
  book: {
    id: number;
    title: string;
    author: {
      firstName: string;
      lastName: string;
    };
    category: {
      name: string;
    };
  };
  createdAt: string;
}

export const favoriteService = {
  /**
   * Get all favorites for a user
   */
  getUserFavorites: async (userId: number): Promise<Favorite[]> => {
    return await httpClient.get(`/api/favorites/user/${userId}`);
  },

  /**
   * Add a book to user's favorites
   */
  addFavorite: async (userId: number, bookId: number): Promise<Favorite> => {
    return await httpClient.post(`/api/favorites/user/${userId}/book/${bookId}`);
  },

  /**
   * Remove a book from user's favorites
   */
  removeFavorite: async (userId: number, bookId: number): Promise<void> => {
    await httpClient.delete(`/api/favorites/user/${userId}/book/${bookId}`);
  },

  /**
   * Toggle favorite status
   */
  toggleFavorite: async (userId: number, bookId: number): Promise<{ isFavorited: boolean; message: string }> => {
    return await httpClient.post(`/api/favorites/user/${userId}/book/${bookId}/toggle`);
  },

  /**
   * Check if a book is favorited by a user
   */
  getFavoriteStatus: async (userId: number, bookId: number): Promise<{ isFavorited: boolean }> => {
    return await httpClient.get(`/api/favorites/user/${userId}/book/${bookId}/status`);
  },

  /**
   * Get favorite count for a book
   */
  getFavoriteCount: async (bookId: number): Promise<{ count: number }> => {
    return await httpClient.get(`/api/favorites/book/${bookId}/count`);
  }
};