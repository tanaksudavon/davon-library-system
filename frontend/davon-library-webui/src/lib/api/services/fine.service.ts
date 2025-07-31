import { httpClient } from '../client';
import { API_CONFIG } from '../config';
import { Fine, FineStatus } from '../types';

export interface FineCreateRequest {
  userId: number;
  loanId?: number;
  amount: number;
  reason: string;
  issuedDate: string;
}

export interface FineUpdateRequest {
  amount?: number;
  reason?: string;
  paidDate?: string;
  status?: FineStatus;
  paymentMethod?: string;
  notes?: string;
}

export class FineService {
  /**
   * Get all fines
   */
  async getAllFines(): Promise<Fine[]> {
    try {
      return await httpClient.get<Fine[]>(API_CONFIG.ENDPOINTS.FINES.BASE);
    } catch (error) {
      console.error('Failed to fetch fines:', error);
      throw error;
    }
  }

  /**
   * Get fine by ID
   */
  async getFineById(id: number): Promise<Fine> {
    try {
      return await httpClient.get<Fine>(API_CONFIG.ENDPOINTS.FINES.BY_ID(id));
    } catch (error) {
      console.error(`Failed to fetch fine with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Get fines by user ID
   */
  async getFinesByUserId(userId: number): Promise<Fine[]> {
    try {
      return await httpClient.get<Fine[]>(API_CONFIG.ENDPOINTS.FINES.USER_FINES(userId));
    } catch (error) {
      console.error(`Failed to fetch fines for user ${userId}:`, error);
      throw error;
    }
  }

  /**
   * Get total fines amount for a user from the server
   */
  async getUserTotalFines(userId: number): Promise<number> {
    try {
      const response = await httpClient.get<{ totalFines: number }>(
        API_CONFIG.ENDPOINTS.FINES.USER_TOTAL(userId)
      );
      return response.totalFines;
    } catch (error) {
      console.error(`Failed to fetch total fines for user ${userId}:`, error);
      throw error;
    }
  }

  /**
   * Create new fine
   */
  async createFine(fineData: FineCreateRequest): Promise<Fine> {
    try {
      return await httpClient.post<Fine>(
        API_CONFIG.ENDPOINTS.FINES.BASE,
        fineData
      );
    } catch (error) {
      console.error('Failed to create fine:', error);
      throw error;
    }
  }

  /**
   * Update existing fine
   */
  async updateFine(id: number, fineData: FineUpdateRequest): Promise<Fine> {
    try {
      return await httpClient.put<Fine>(
        API_CONFIG.ENDPOINTS.FINES.BY_ID(id),
        fineData
      );
    } catch (error) {
      console.error(`Failed to update fine with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Delete fine
   */
  async deleteFine(id: number): Promise<void> {
    try {
      await httpClient.delete<void>(API_CONFIG.ENDPOINTS.FINES.BY_ID(id));
    } catch (error) {
      console.error(`Failed to delete fine with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Pay a fine
   */
  async payFine(fineId: number, paymentMethod?: string): Promise<Fine> {
    try {
      return await httpClient.post<Fine>(API_CONFIG.ENDPOINTS.FINES.PAY(fineId), {
        paymentMethod
      });
    } catch (error) {
      console.error(`Failed to pay fine ${fineId}:`, error);
      throw error;
    }
  }

  /**
   * Waive a fine
   */
  async waiveFine(fineId: number, reason?: string): Promise<Fine> {
    try {
      return await this.updateFine(fineId, {
        status: FineStatus.WAIVED,
        paidDate: new Date().toISOString(),
        notes: reason
      });
    } catch (error) {
      console.error(`Failed to waive fine ${fineId}:`, error);
      throw error;
    }
  }

  /**
   * Get pending fines for a user
   */
  async getPendingFinesByUserId(userId: number): Promise<Fine[]> {
    try {
      const allFines = await this.getFinesByUserId(userId);
      return allFines.filter(fine => fine.status === FineStatus.PENDING);
    } catch (error) {
      console.error(`Failed to fetch pending fines for user ${userId}:`, error);
      throw error;
    }
  }

  /**
   * Get total pending amount for a user
   */
  async getTotalPendingAmount(userId: number): Promise<number> {
    try {
      const pendingFines = await this.getPendingFinesByUserId(userId);
      return pendingFines.reduce((total, fine) => total + fine.amount, 0);
    } catch (error) {
      console.error(`Failed to calculate total pending amount for user ${userId}:`, error);
      return 0;
    }
  }

  /**
   * Get fine statistics
   */
  async getFineStats(): Promise<{
    totalFines: number;
    totalPending: number;
    totalPaid: number;
    totalWaived: number;
    totalPendingAmount: number;
    totalPaidAmount: number;
  }> {
    try {
      const allFines = await this.getAllFines();
      
      const pendingFines = allFines.filter(f => f.status === FineStatus.PENDING);
      const paidFines = allFines.filter(f => f.status === FineStatus.PAID);
      const waivedFines = allFines.filter(f => f.status === FineStatus.WAIVED);

      return {
        totalFines: allFines.length,
        totalPending: pendingFines.length,
        totalPaid: paidFines.length,
        totalWaived: waivedFines.length,
        totalPendingAmount: pendingFines.reduce((sum, fine) => sum + fine.amount, 0),
        totalPaidAmount: paidFines.reduce((sum, fine) => sum + fine.amount, 0)
      };
    } catch (error) {
      console.error('Failed to get fine statistics:', error);
      return {
        totalFines: 0,
        totalPending: 0,
        totalPaid: 0,
        totalWaived: 0,
        totalPendingAmount: 0,
        totalPaidAmount: 0
      };
    }
  }

  /**
   * Check if user has outstanding fines
   */
  async hasOutstandingFines(userId: number): Promise<boolean> {
    try {
      const pendingFines = await this.getPendingFinesByUserId(userId);
      return pendingFines.length > 0;
    } catch (error) {
      console.error(`Failed to check outstanding fines for user ${userId}:`, error);
      return false;
    }
  }

  /**
   * Generate overdue fine for a loan
   */
  async generateOverdueFine(
    userId: number, 
    loanId: number, 
    daysOverdue: number,
    finePerDay: number = 0.50
  ): Promise<Fine> {
    try {
      const amount = daysOverdue * finePerDay;
      return await this.createFine({
        userId,
        loanId,
        amount,
        reason: `Overdue fine for ${daysOverdue} days at $${finePerDay}/day`,
        issuedDate: new Date().toISOString()
      });
    } catch (error) {
      console.error('Failed to generate overdue fine:', error);
      throw error;
    }
  }
}

// Create and export singleton instance
export const fineService = new FineService(); 