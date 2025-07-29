import { httpClient } from '../client';
import { API_CONFIG } from '../config';
import { 
  User, 
  UserCreateRequest, 
  UserUpdateRequest, 
  UserSearchFilters,
  PaginatedResponse 
} from '../types';

export class UserService {
  /**
   * Get all users
   */
  async getAllUsers(): Promise<User[]> {
    try {
      return await httpClient.get<User[]>(API_CONFIG.ENDPOINTS.USERS.BASE);
    } catch (error) {
      console.error('Failed to fetch users:', error);
      throw error;
    }
  }

  /**
   * Get user by ID
   */
  async getUserById(id: number): Promise<User> {
    try {
      return await httpClient.get<User>(API_CONFIG.ENDPOINTS.USERS.BY_ID(id));
    } catch (error) {
      console.error(`Failed to fetch user with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Create new user
   */
  async createUser(userData: UserCreateRequest): Promise<User> {
    try {
      return await httpClient.post<User>(
        API_CONFIG.ENDPOINTS.USERS.BASE,
        userData
      );
    } catch (error) {
      console.error('Failed to create user:', error);
      throw error;
    }
  }

  /**
   * Update existing user
   */
  async updateUser(id: number, userData: UserUpdateRequest): Promise<User> {
    try {
      const requestData = {
        ...userData,
        id, // Set id after spreading userData to avoid duplication
      };

      return await httpClient.put<User>(
        API_CONFIG.ENDPOINTS.USERS.BY_ID(id),
        requestData
      );
    } catch (error) {
      console.error(`Failed to update user with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Delete user
   */
  async deleteUser(id: number): Promise<void> {
    try {
      await httpClient.delete<void>(API_CONFIG.ENDPOINTS.USERS.BY_ID(id));
    } catch (error) {
      console.error(`Failed to delete user with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Search users with filters
   */
  async searchUsers(filters: UserSearchFilters): Promise<User[]> {
    try {
      // Build query parameters
      const params: Record<string, string> = {};
      
      if (filters.username) params.username = filters.username;
      if (filters.email) params.email = filters.email;
      if (filters.firstName) params.firstName = filters.firstName;
      if (filters.lastName) params.lastName = filters.lastName;
      if (filters.role) params.role = filters.role;
      if (filters.status) params.status = filters.status;

      // For now, get all users and filter client-side
      const allUsers = await this.getAllUsers();
      return this.applyFilters(allUsers, filters);
    } catch (error) {
      console.error('Failed to search users:', error);
      throw error;
    }
  }

  /**
   * Get users with pagination
   */
  async getUsersWithPagination(
    page: number = 1,
    limit: number = 10,
    filters?: UserSearchFilters
  ): Promise<PaginatedResponse<User>> {
    try {
      // Get all users
      const allUsers = await this.getAllUsers();
      
      // Apply filters if provided
      let filteredUsers = allUsers;
      if (filters) {
        filteredUsers = this.applyFilters(allUsers, filters);
      }

      // Apply pagination
      const startIndex = (page - 1) * limit;
      const endIndex = startIndex + limit;
      const paginatedUsers = filteredUsers.slice(startIndex, endIndex);

      return {
        data: paginatedUsers,
        currentPage: page,
        totalPages: Math.ceil(filteredUsers.length / limit),
        totalItems: filteredUsers.length,
        itemsPerPage: limit,
      };
    } catch (error) {
      console.error('Failed to fetch paginated users:', error);
      throw error;
    }
  }

  /**
   * Apply filters to users array (client-side filtering)
   */
  private applyFilters(users: User[], filters: UserSearchFilters): User[] {
    return users.filter(user => {
      // Username filter
      if (filters.username && !user.username.toLowerCase().includes(filters.username.toLowerCase())) {
        return false;
      }

      // Email filter
      if (filters.email && !user.email.toLowerCase().includes(filters.email.toLowerCase())) {
        return false;
      }

      // First name filter
      if (filters.firstName && !user.firstName.toLowerCase().includes(filters.firstName.toLowerCase())) {
        return false;
      }

      // Last name filter
      if (filters.lastName && !user.lastName.toLowerCase().includes(filters.lastName.toLowerCase())) {
        return false;
      }

      // Role filter
      if (filters.role && user.role !== filters.role) {
        return false;
      }

      // Status filter
      if (filters.status && user.status !== filters.status) {
        return false;
      }

      return true;
    });
  }

  /**
   * Get users by role
   */
  async getUsersByRole(role: string): Promise<User[]> {
    try {
      const allUsers = await this.getAllUsers();
      return allUsers.filter(user => user.role === role);
    } catch (error) {
      console.error(`Failed to fetch users with role ${role}:`, error);
      throw error;
    }
  }

  /**
   * Get all members
   */
  async getMembers(): Promise<User[]> {
    return this.getUsersByRole('MEMBER');
  }

  /**
   * Get all librarians
   */
  async getLibrarians(): Promise<User[]> {
    return this.getUsersByRole('LIBRARIAN');
  }

  /**
   * Update user profile (for current user)
   */
  async updateProfile(userData: Partial<Omit<UserUpdateRequest, 'id'>>): Promise<User> {
    try {
      // Get current user ID from auth service or context
      const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
      if (!currentUser.id) {
        throw new Error('No authenticated user found');
      }

      // Create full UserUpdateRequest with id
      const updateRequest: UserUpdateRequest = {
        ...userData,
        id: currentUser.id,
      };

      return await this.updateUser(currentUser.id, updateRequest);
    } catch (error) {
      console.error('Failed to update user profile:', error);
      throw error;
    }
  }

  /**
   * Change user password
   */
  async changePassword(currentPassword: string, newPassword: string): Promise<void> {
    try {
      const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
      if (!currentUser.id) {
        throw new Error('No authenticated user found');
      }

      // This would typically be a separate endpoint in the backend
      const requestData = {
        currentPassword,
        newPassword,
      };

      await httpClient.post(
        `/api/users/${currentUser.id}/change-password`,
        requestData
      );
    } catch (error) {
      console.error('Failed to change password:', error);
      throw error;
    }
  }

  /**
   * Get user statistics
   */
  async getUserStats(userId: number): Promise<{
    totalLoans: number;
    activeLoans: number;
    overdueLoans: number;
    totalFines: number;
    pendingFines: number;
  }> {
    try {
      // This would typically be a separate endpoint
      // For now, we'll return mock data
      return {
        totalLoans: 0,
        activeLoans: 0,
        overdueLoans: 0,
        totalFines: 0,
        pendingFines: 0,
      };
    } catch (error) {
      console.error(`Failed to fetch stats for user ${userId}:`, error);
      throw error;
    }
  }

  /**
   * Activate user account
   */
  async activateUser(userId: number): Promise<User> {
    try {
      return await httpClient.patch<User>(
        API_CONFIG.ENDPOINTS.USERS.BY_ID(userId),
        { status: 'ACTIVE' }
      );
    } catch (error) {
      console.error(`Failed to activate user ${userId}:`, error);
      throw error;
    }
  }

  /**
   * Deactivate user account
   */
  async deactivateUser(userId: number): Promise<User> {
    try {
      return await httpClient.patch<User>(
        API_CONFIG.ENDPOINTS.USERS.BY_ID(userId),
        { status: 'INACTIVE' }
      );
    } catch (error) {
      console.error(`Failed to deactivate user ${userId}:`, error);
      throw error;
    }
  }

  /**
   * Check if username is available
   */
  async isUsernameAvailable(username: string): Promise<boolean> {
    try {
      const users = await this.getAllUsers();
      return !users.some(user => user.username.toLowerCase() === username.toLowerCase());
    } catch (error) {
      console.error('Failed to check username availability:', error);
      return false;
    }
  }

  /**
   * Check if email is available
   */
  async isEmailAvailable(email: string): Promise<boolean> {
    try {
      const users = await this.getAllUsers();
      return !users.some(user => user.email.toLowerCase() === email.toLowerCase());
    } catch (error) {
      console.error('Failed to check email availability:', error);
      return false;
    }
  }
}

// Create and export singleton instance
export const userService = new UserService(); 