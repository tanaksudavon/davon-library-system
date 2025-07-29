import { httpClient } from '../client';
import { API_CONFIG } from '../config';
import { 
  Loan, 
  LoanCreateRequest, 
  LoanStatus,
  PaginatedResponse 
} from '../types';

export class LoanService {
  /**
   * Get all loans
   */
  async getAllLoans(): Promise<Loan[]> {
    try {
      return await httpClient.get<Loan[]>(API_CONFIG.ENDPOINTS.LOANS.BASE);
    } catch (error) {
      console.error('Failed to fetch loans:', error);
      throw error;
    }
  }

  /**
   * Get loan by ID
   */
  async getLoanById(id: number): Promise<Loan> {
    try {
      return await httpClient.get<Loan>(API_CONFIG.ENDPOINTS.LOANS.BY_ID(id));
    } catch (error) {
      console.error(`Failed to fetch loan with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Get loans by user ID
   */
  async getLoansByUserId(userId: number): Promise<Loan[]> {
    try {
      return await httpClient.get<Loan[]>(API_CONFIG.ENDPOINTS.LOANS.BY_USER(userId));
    } catch (error) {
      console.error(`Failed to fetch loans for user ${userId}:`, error);
      throw error;
    }
  }

  /**
   * Create new loan (checkout book)
   */
  async createLoan(loanData: LoanCreateRequest): Promise<Loan> {
    try {
      // Transform the request to match backend expectations
      const requestData = {
        book: { id: loanData.bookId },
        user: { id: loanData.userId },
        borrowDate: new Date().toISOString().split('T')[0], // Today's date
        dueDate: loanData.dueDate,
        status: 'ACTIVE' as LoanStatus,
        notes: loanData.notes,
      };

      return await httpClient.post<Loan>(
        API_CONFIG.ENDPOINTS.LOANS.BASE,
        requestData
      );
    } catch (error) {
      console.error('Failed to create loan:', error);
      throw error;
    }
  }

  /**
   * Return book (update loan status)
   */
  async returnBook(loanId: number): Promise<Loan> {
    try {
      const requestData = {
        status: 'RETURNED' as LoanStatus,
        returnDate: new Date().toISOString().split('T')[0], // Today's date
      };

      return await httpClient.put<Loan>(
        API_CONFIG.ENDPOINTS.LOANS.BY_ID(loanId),
        requestData
      );
    } catch (error) {
      console.error(`Failed to return book for loan ${loanId}:`, error);
      throw error;
    }
  }

  /**
   * Renew loan (extend due date)
   */
  async renewLoan(loanId: number, newDueDate: string): Promise<Loan> {
    try {
      const requestData = {
        dueDate: newDueDate,
      };

      return await httpClient.put<Loan>(
        API_CONFIG.ENDPOINTS.LOANS.BY_ID(loanId),
        requestData
      );
    } catch (error) {
      console.error(`Failed to renew loan ${loanId}:`, error);
      throw error;
    }
  }

  /**
   * Get current user's loans
   */
  async getCurrentUserLoans(): Promise<Loan[]> {
    try {
      const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
      if (!currentUser.id) {
        throw new Error('No authenticated user found');
      }

      return await this.getLoansByUserId(currentUser.id);
    } catch (error) {
      console.error('Failed to fetch current user loans:', error);
      throw error;
    }
  }

  /**
   * Get active loans
   */
  async getActiveLoans(): Promise<Loan[]> {
    try {
      const allLoans = await this.getAllLoans();
      return allLoans.filter(loan => loan.status === 'ACTIVE');
    } catch (error) {
      console.error('Failed to fetch active loans:', error);
      throw error;
    }
  }

  /**
   * Get overdue loans
   */
  async getOverdueLoans(): Promise<Loan[]> {
    try {
      const allLoans = await this.getAllLoans();
      const today = new Date();
      
      return allLoans.filter(loan => {
        if (loan.status !== 'ACTIVE') return false;
        const dueDate = new Date(loan.dueDate);
        return dueDate < today;
      });
    } catch (error) {
      console.error('Failed to fetch overdue loans:', error);
      throw error;
    }
  }

  /**
   * Get loans with pagination
   */
  async getLoansWithPagination(
    page: number = 1,
    limit: number = 10,
    status?: LoanStatus
  ): Promise<PaginatedResponse<Loan>> {
    try {
      // Get all loans
      let allLoans = await this.getAllLoans();
      
      // Filter by status if provided
      if (status) {
        allLoans = allLoans.filter(loan => loan.status === status);
      }

      // Apply pagination
      const startIndex = (page - 1) * limit;
      const endIndex = startIndex + limit;
      const paginatedLoans = allLoans.slice(startIndex, endIndex);

      return {
        data: paginatedLoans,
        currentPage: page,
        totalPages: Math.ceil(allLoans.length / limit),
        totalItems: allLoans.length,
        itemsPerPage: limit,
      };
    } catch (error) {
      console.error('Failed to fetch paginated loans:', error);
      throw error;
    }
  }

  /**
   * Check if user can borrow book
   */
  async canUserBorrowBook(userId: number, bookId: number): Promise<{
    canBorrow: boolean;
    reason?: string;
  }> {
    try {
      // Get user's active loans
      const userLoans = await this.getLoansByUserId(userId);
      const activeLoans = userLoans.filter(loan => loan.status === 'ACTIVE');

      // Check loan limit (example: max 5 books)
      const maxLoans = 5;
      if (activeLoans.length >= maxLoans) {
        return {
          canBorrow: false,
          reason: `Maximum loan limit reached (${maxLoans} books)`,
        };
      }

      // Check if user already has this book
      const hasBook = activeLoans.some(loan => loan.book.id === bookId);
      if (hasBook) {
        return {
          canBorrow: false,
          reason: 'User already has this book on loan',
        };
      }

      // Check for overdue books
      const overdueLoans = activeLoans.filter(loan => {
        const dueDate = new Date(loan.dueDate);
        return dueDate < new Date();
      });

      if (overdueLoans.length > 0) {
        return {
          canBorrow: false,
          reason: 'User has overdue books that must be returned first',
        };
      }

      return { canBorrow: true };
    } catch (error) {
      console.error('Failed to check if user can borrow book:', error);
      return {
        canBorrow: false,
        reason: 'Unable to verify borrowing eligibility',
      };
    }
  }

  /**
   * Get loan statistics
   */
  async getLoanStats(): Promise<{
    totalLoans: number;
    activeLoans: number;
    overdueLoans: number;
    returnedLoans: number;
  }> {
    try {
      const allLoans = await this.getAllLoans();
      const today = new Date();

      const stats = {
        totalLoans: allLoans.length,
        activeLoans: 0,
        overdueLoans: 0,
        returnedLoans: 0,
      };

      allLoans.forEach(loan => {
        switch (loan.status) {
          case 'ACTIVE':
            stats.activeLoans++;
            // Check if overdue
            const dueDate = new Date(loan.dueDate);
            if (dueDate < today) {
              stats.overdueLoans++;
            }
            break;
          case 'RETURNED':
            stats.returnedLoans++;
            break;
        }
      });

      return stats;
    } catch (error) {
      console.error('Failed to fetch loan statistics:', error);
      throw error;
    }
  }
}

// Create and export singleton instance
export const loanService = new LoanService(); 