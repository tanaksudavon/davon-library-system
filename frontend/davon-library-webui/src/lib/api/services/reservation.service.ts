import { httpClient } from '../client';
import { API_CONFIG } from '../config';
import { Reservation, User, Book } from '../types';

export interface ReservationCreateRequest {
  bookId: number;
  userId: number;
}

export class ReservationService {
  /**
   * Create a new reservation
   */
  async createReservation(bookId: number, userId: number): Promise<Reservation> {
    try {
      return await httpClient.post<Reservation>(
        `/api/reservations/book/${bookId}/user/${userId}`,
        {}
      );
    } catch (error) {
      console.error(`Failed to create reservation for book ${bookId}:`, error);
      throw error;
    }
  }

  /**
   * Cancel a reservation
   */
  async cancelReservation(reservationId: number, userId: number): Promise<Reservation> {
    try {
      return await httpClient.delete<Reservation>(
        `/api/reservations/${reservationId}/user/${userId}`
      );
    } catch (error) {
      console.error(`Failed to cancel reservation ${reservationId}:`, error);
      throw error;
    }
  }

  /**
   * Get user's active reservations
   */
  async getUserReservations(userId: number): Promise<Reservation[]> {
    try {
      return await httpClient.get<Reservation[]>(
        API_CONFIG.ENDPOINTS.RESERVATIONS.BY_USER(userId)
      );
    } catch (error) {
      console.error(`Failed to fetch reservations for user ${userId}:`, error);
      throw error;
    }
  }

  /**
   * Get reservation queue for a specific book
   */
  async getBookReservationQueue(bookId: number): Promise<Reservation[]> {
    try {
      return await httpClient.get<Reservation[]>(
        `/api/reservations/book/${bookId}/queue`
      );
    } catch (error) {
      console.error(`Failed to fetch reservation queue for book ${bookId}:`, error);
      throw error;
    }
  }

  /**
   * Get all reservations (admin only)
   */
  async getAllReservations(): Promise<Reservation[]> {
    try {
      return await httpClient.get<Reservation[]>(API_CONFIG.ENDPOINTS.RESERVATIONS.BASE);
    } catch (error) {
      console.error('Failed to fetch all reservations:', error);
      throw error;
    }
  }

  /**
   * Check if user has already reserved a book
   */
  async hasUserReservedBook(userId: number, bookId: number): Promise<boolean> {
    try {
      const userReservations = await this.getUserReservations(userId);
      return userReservations.some(reservation => 
        reservation.book.id === bookId && 
        reservation.status === 'ACTIVE'
      );
    } catch (error) {
      console.error('Failed to check user reservation status:', error);
      return false;
    }
  }

  /**
   * Get user's position in reservation queue
   */
  async getUserPositionInQueue(userId: number, bookId: number): Promise<number> {
    try {
      const queue = await this.getBookReservationQueue(bookId);
      const position = queue.findIndex(reservation => reservation.user.id === userId);
      return position >= 0 ? position + 1 : 0; // Return 1-based position, 0 if not found
    } catch (error) {
      console.error('Failed to get user position in queue:', error);
      return 0;
    }
  }

  /**
   * Get reservation statistics
   */
  async getReservationStats(): Promise<{
    totalActive: number;
    totalExpired: number;
    totalFulfilled: number;
    totalCancelled: number;
  }> {
    try {
      const reservations = await this.getAllReservations();
      
      return {
        totalActive: reservations.filter(r => r.status === 'ACTIVE').length,
        totalExpired: reservations.filter(r => r.status === 'EXPIRED').length,
        totalFulfilled: reservations.filter(r => r.status === 'FULFILLED').length,
        totalCancelled: reservations.filter(r => r.status === 'CANCELLED').length,
      };
    } catch (error) {
      console.error('Failed to get reservation statistics:', error);
      return {
        totalActive: 0,
        totalExpired: 0,
        totalFulfilled: 0,
        totalCancelled: 0,
      };
    }
  }

  /**
   * Expire old reservations (admin function)
   */
  async expireOldReservations(): Promise<void> {
    try {
      await httpClient.post<void>('/api/reservations/expire-old', {});
    } catch (error) {
      console.error('Failed to expire old reservations:', error);
      throw error;
    }
  }

  /**
   * Check if a book can be reserved
   */
  async canReserveBook(bookId: number, userId: number): Promise<{
    canReserve: boolean;
    reason?: string;
  }> {
    try {
      // Check if user already has this book reserved
      const hasReserved = await this.hasUserReservedBook(userId, bookId);
      if (hasReserved) {
        return {
          canReserve: false,
          reason: 'You have already reserved this book'
        };
      }

      // Additional validation can be added here
      return { canReserve: true };
    } catch (error) {
      console.error('Failed to check if book can be reserved:', error);
      return {
        canReserve: false,
        reason: 'Unable to check reservation status'
      };
    }
  }
}

// Create and export singleton instance
export const reservationService = new ReservationService(); 