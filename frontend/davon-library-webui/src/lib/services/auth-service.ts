import { authService as apiAuthService } from '../api/services';
import { LoginRequest, RegisterRequest, User } from '../api/types';

class AuthService {
    constructor() {
        // Initialize auth state on service creation
        this.initializeAuth();
    }

    /**
     * Login user with username and password
     */
    async login(credentials: LoginRequest): Promise<User> {
        return await apiAuthService.login(credentials);
    }

    /**
     * Register new user
     */
    async register(userData: RegisterRequest): Promise<User> {
        return await apiAuthService.register(userData);
    }

    /**
     * Logout user and clear stored data
     */
    logout(): void {
        apiAuthService.logout();
    }

    /**
     * Clear all authentication data (for debugging)
     */
    clearAllAuthData(): void {
        apiAuthService.clearAllAuthData();
    }

    /**
     * Get current user from storage
     */
    getCurrentUser(): User | null {
        return apiAuthService.getCurrentUser();
    }

    /**
     * Check if user is authenticated
     */
    isAuthenticated(): boolean {
        return apiAuthService.isAuthenticated();
    }

    /**
     * Get stored authentication token
     */
    getToken(): string | null {
        return apiAuthService.getToken();
    }

    /**
     * Check if current user has specific role
     */
    hasRole(role: string): boolean {
        return apiAuthService.hasRole(role);
    }

    /**
     * Check if current user is admin/librarian
     */
    isLibrarian(): boolean {
        return apiAuthService.isLibrarian();
    }

    /**
     * Check if current user is member
     */
    isMember(): boolean {
        return apiAuthService.isMember();
    }

    /**
     * Validate current session
     */
    async validateSession(): Promise<boolean> {
        return await apiAuthService.validateSession();
    }

    /**
     * Initialize auth state on app startup
     */
    initializeAuth(): User | null {
        return apiAuthService.initializeAuth();
    }

    /**
     * Get sample credentials for testing
     * TODO: Remove this in production
     */
    getSampleCredentials(): Array<{email: string, password: string, role: string}> {
        return [
            { email: 'admin@library.com', password: 'admin123', role: 'LIBRARIAN' },
            { email: 'user@library.com', password: 'user123', role: 'MEMBER' },
            { email: 'guest@library.com', password: 'guest123', role: 'GUEST' },
        ];
    }
}

export const authService = new AuthService(); 