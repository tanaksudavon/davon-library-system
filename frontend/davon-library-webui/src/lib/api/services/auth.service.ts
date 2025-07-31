import { httpClient } from '../client';
import { API_CONFIG } from '../config';
import { LoginRequest, RegisterRequest, User } from '../types';

export class AuthService {
  /**
   * Login user with username and password
   */
  async login(credentials: LoginRequest): Promise<User> {
    try {
      console.log('Attempting login for username:', credentials.username);
      
      const response = await httpClient.post<User>(
        API_CONFIG.ENDPOINTS.AUTH.LOGIN,
        credentials
      );

      console.log('Login response received:', {
        id: response.id,
        username: response.username,
        firstName: response.firstName,
        lastName: response.lastName,
        email: response.email,
        role: response.role
      });

      // Store user data in localStorage
      if (typeof window !== 'undefined') {
        localStorage.setItem('user', JSON.stringify(response));
        // Generate a simple token (your backend might provide a real JWT)
        const token = btoa(`${credentials.username}:${Date.now()}`);
        localStorage.setItem('token', token);
        httpClient.setAuthToken(token);
        console.log('User data stored in localStorage');

        // Update Zustand store to trigger component updates
        const { useAuthStore } = await import('../../store/auth-store');
        useAuthStore.getState().setAuth(response, token);
        console.log('Auth store updated');
      }

      return response;
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    }
  }

  /**
   * Register new user
   */
  async register(userData: RegisterRequest): Promise<User> {
    try {
      const response = await httpClient.post<User>(
        API_CONFIG.ENDPOINTS.AUTH.REGISTER,
        userData
      );

      // Auto-login after successful registration
      const loginCredentials: LoginRequest = {
        username: userData.username,
        password: userData.password,
      };

      return await this.login(loginCredentials);
    } catch (error) {
      console.error('Registration failed:', error);
      throw error;
    }
  }

  /**
   * Clear all authentication data (for debugging)
   */
  clearAllAuthData(): void {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      localStorage.removeItem('login_success');
      localStorage.removeItem('redirecting_to_login');
      // Clear any other auth-related data
      Object.keys(localStorage).forEach(key => {
        if (key.includes('auth') || key.includes('user') || key.includes('login')) {
          localStorage.removeItem(key);
        }
      });
    }
    httpClient.clearAuthToken();
    console.log('All authentication data cleared');
  }

  /**
   * Logout user and clear stored data
   */
  logout(): void {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      localStorage.removeItem('login_success');
      localStorage.removeItem('redirecting_to_login');

      // Update Zustand store to trigger component updates
      const { useAuthStore } = require('../../store/auth-store');
      useAuthStore.getState().clearAuth();
      console.log('Auth store cleared');
    }
    httpClient.clearAuthToken();
  }

  /**
   * Get current user from localStorage
   */
  getCurrentUser(): User | null {
    if (typeof window === 'undefined') return null;

    try {
      const userJson = localStorage.getItem('user');
      return userJson ? JSON.parse(userJson) : null;
    } catch (error) {
      console.error('Error parsing user from localStorage:', error);
      return null;
    }
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    if (typeof window === 'undefined') return false;
    
    const user = this.getCurrentUser();
    const token = localStorage.getItem('token');
    
    return !!(user && token);
  }

  /**
   * Get stored authentication token
   */
  getToken(): string | null {
    if (typeof window === 'undefined') return null;
    return localStorage.getItem('token');
  }

  /**
   * Check if current user has specific role
   */
  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user?.role === role;
  }

  /**
   * Check if current user is admin/librarian
   */
  isLibrarian(): boolean {
    return this.hasRole('LIBRARIAN');
  }

  /**
   * Check if current user is member
   */
  isMember(): boolean {
    return this.hasRole('MEMBER');
  }

  /**
   * Validate current session (optional - for JWT token validation)
   */
  async validateSession(): Promise<boolean> {
    try {
      if (!this.isAuthenticated()) {
        return false;
      }

      // You could add an endpoint to validate the current session/token
      // For now, we'll just check if we have valid stored data
      const user = this.getCurrentUser();
      return !!user;
    } catch (error) {
      console.error('Session validation failed:', error);
      this.logout(); // Clear invalid session
      return false;
    }
  }

  /**
   * Initialize auth state on app startup
   */
  initializeAuth(): User | null {
    if (typeof window === 'undefined') return null;

    const user = this.getCurrentUser();
    const token = this.getToken();

    if (user && token) {
      httpClient.setAuthToken(token);
      return user;
    }

    return null;
  }
}

// Create and export singleton instance
export const authService = new AuthService(); 