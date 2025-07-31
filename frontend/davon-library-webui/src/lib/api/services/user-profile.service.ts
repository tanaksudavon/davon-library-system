import { User, Loan, Reservation, Fine, Book } from '../types';
import { userService } from './user.service';
import { loanService } from './loan.service';
import { reservationService } from './reservation.service';
import { fineService } from './fine.service';

export interface UserProfileData {
  user: User;
  currentLoans: Loan[];
  loanHistory: Loan[];
  reservations: Reservation[];
  fines: Fine[];
  totalFinesAmount: number;
  borrowedBooks: Book[];
  reservedBooks: Book[];
}

export class UserProfileService {
  /**
   * Get comprehensive profile data for a user
   */
  async getUserProfileData(userId: number): Promise<UserProfileData> {
    try {
      // Fetch all data in parallel
      const [
        user,
        loans, 
        reservations,
        fines,
        totalFinesAmount
      ] = await Promise.all([
        userService.getUserById(userId),
        loanService.getLoansByUserId(userId),
        reservationService.getUserReservations(userId),
        fineService.getFinesByUserId(userId),
        fineService.getUserTotalFines(userId)
      ]);

      // FIXED: Separate current loans from history based on return date
      // Current loans = no return date (book is still with user)
      const currentLoans = loans.filter(loan => !loan.returnDate);
      // Loan history = has return date (book was returned)
      const loanHistory = loans.filter(loan => !!loan.returnDate);

      // Extract books from loans and reservations
      const borrowedBooks = currentLoans.map(loan => loan.book).filter(Boolean);
      const reservedBooks = reservations.map(reservation => reservation.book).filter(Boolean);

      return {
        user,
        currentLoans,
        loanHistory,
        reservations,
        fines,
        totalFinesAmount,
        borrowedBooks,
        reservedBooks
      };
    } catch (error) {
      console.error(`Failed to fetch profile data for user ${userId}:`, error);
      throw error;
    }
  }

  /**
   * Get user's current borrowed books (no return date)
   */
  async getUserBorrowedBooks(userId: number): Promise<Book[]> {
    try {
      const loans = await loanService.getLoansByUserId(userId);
      // Only current loans (no return date)
      const currentLoans = loans.filter(loan => !loan.returnDate);
      return currentLoans.map(loan => loan.book).filter(Boolean);
    } catch (error) {
      console.error(`Failed to fetch borrowed books for user ${userId}:`, error);
      throw error;
    }
  }

  /**
   * Get user's reserved books
   */
  async getUserReservedBooks(userId: number): Promise<Book[]> {
    try {
      const reservations = await reservationService.getUserReservations(userId);
      return reservations.map(reservation => reservation.book).filter(Boolean);
    } catch (error) {
      console.error(`Failed to fetch reserved books for user ${userId}:`, error);
      throw error;
    }
  }

  /**
   * Get user's loan history with fine information
   */
  async getUserLoanHistory(userId: number): Promise<(Loan & { fine?: Fine })[]> {
    try {
      const [loans, fines] = await Promise.all([
        loanService.getLoansByUserId(userId),
        fineService.getFinesByUserId(userId)
      ]);

      // Create a map of fines by loan ID for quick lookup
      const finesByLoanId = new Map<number, Fine>();
      fines.forEach(fine => {
        if (fine.loan?.id) {
          finesByLoanId.set(fine.loan.id, fine);
        }
      });

      // Attach fine information to loans
      return loans.map(loan => ({
        ...loan,
        fine: finesByLoanId.get(loan.id)
      }));
    } catch (error) {
      console.error(`Failed to fetch loan history for user ${userId}:`, error);
      throw error;
    }
  }

  /**
   * Get user's reservation queue position for a book
   */
  async getUserReservationPosition(userId: number, bookId: number): Promise<number | null> {
    try {
      const queue = await reservationService.getBookReservationQueue(bookId);
      const userReservation = queue.find(reservation => 
        reservation && reservation.user && reservation.user.id === userId
      );
      
      if (!userReservation) return null;
      
      return queue.findIndex(reservation => reservation.id === userReservation.id) + 1;
    } catch (error) {
      console.error(`Failed to get reservation position for user ${userId} and book ${bookId}:`, error);
      return null;
    }
  }
}

// Create and export singleton instance
export const userProfileService = new UserProfileService(); 