import { User, UserLoginInput, AuthResponse } from '@/types/user';
import { MOCK_ADMIN_USER, MOCK_NORMAL_USER } from './mockUser';
import { userService } from './user-service';

class AuthService {
    constructor() {
        // Initialize immediately if running in browser
        if (typeof window !== 'undefined') {
            // Force reinitialize users in localStorage on service creation
            this.forceReinitializeUsers();
        }
    }

    // Initialize local storage with default users if empty
    private initializeUsers() {
        const usersJson = localStorage.getItem('users');
        if (!usersJson || JSON.parse(usersJson).length === 0) {
            console.log('Initializing users in localStorage');
            const initialUsers = [MOCK_ADMIN_USER, MOCK_NORMAL_USER];
            localStorage.setItem('users', JSON.stringify(initialUsers));
        }
    }

    // Force reinitialize users in localStorage (use for testing or when users are corrupted)
    private forceReinitializeUsers() {
        console.log('Force reinitializing users in localStorage');
        localStorage.removeItem('users'); // Clear existing users
        const initialUsers = [MOCK_ADMIN_USER, MOCK_NORMAL_USER];
        localStorage.setItem('users', JSON.stringify(initialUsers));
        console.log('Users reinitialized:', initialUsers);
    }

    async login(email: string, password: string): Promise<AuthResponse> {
        console.log('Login attempt with:', { email });
        
        // Get all users from localStorage
        const users = userService.getAllUsers();
        console.log('Available users:', users);
        
        // Find user with matching email and password
        const user = users.find(u => u.email === email && u.password === password);
        
        if (!user) {
            console.error('Invalid credentials');
            throw new Error('Invalid email or password');
        }
        
        console.log('User found:', user);
        
        // Generate a simple token
        const token = btoa(user.email + ':' + new Date().getTime());
        
        // Store token in localStorage
        localStorage.setItem('token', token);
        
        // Store user object without password
        const { password: _, ...userWithoutPassword } = user;
        localStorage.setItem('user', JSON.stringify(userWithoutPassword));
        
        // Return user info and token
        return {
            user: userWithoutPassword,
            token
        };
    }

    async register(username: string, email: string, password: string): Promise<AuthResponse> {
        // Check if user with this email or username already exists
        const users = userService.getAllUsers();
        
        // Check email
        const existingUserEmail = users.find(u => u.email === email);
        if (existingUserEmail) {
            throw new Error('User with this email already exists');
        }
        
        // Check username
        const existingUserName = users.find(u => u.username === username);
        if (existingUserName) {
            throw new Error('Username already taken');
        }
        
        // Create new user
        const newUser = userService.createUser({
            username,
            email,
            password,
            role: 'user' // Default role for new registrations
        });
        
        // Generate token for automatic login
        const token = btoa(newUser.email + ':' + new Date().getTime());
        
        // Store token in localStorage
        localStorage.setItem('token', token);
        
        // Store user object without password
        const { password: _, ...userWithoutPassword } = newUser;
        localStorage.setItem('user', JSON.stringify(userWithoutPassword));
        
        // Return user info without password and token
        return {
            user: userWithoutPassword,
            token
        };
    }

    logout(): void {
        if (typeof window !== 'undefined') {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            localStorage.removeItem('login_success');
            localStorage.removeItem('redirecting_to_login');
        }
    }

    getToken(): string | null {
        if (typeof window !== 'undefined') {
            return localStorage.getItem('token');
        }
        return null;
    }

    getCurrentUser(): Omit<User, 'password'> | null {
        if (typeof window !== 'undefined') {
            try {
                // First try to get user directly from localStorage
                const userJson = localStorage.getItem('user');
                if (userJson) {
                    return JSON.parse(userJson);
                }
                
                // Fall back to token-based lookup
                const token = this.getToken();
                if (!token) return null;
                
                const decodedToken = atob(token);
                const email = decodedToken.split(':')[0];
                
                const users = userService.getAllUsers();
                const user = users.find(u => u.email === email);
                
                if (!user) return null;
                
                const { password, ...userWithoutPassword } = user;
                localStorage.setItem('user', JSON.stringify(userWithoutPassword));
                
                return userWithoutPassword;
            } catch (error) {
                console.error('Error getting current user:', error);
                this.logout(); // Clear potentially corrupted data
                return null;
            }
        }
        return null;
    }

    // Helper to check what credentials are valid (for debugging)
    getValidCredentials(): {email: string, password: string}[] {
        const users = userService.getAllUsers();
        return users.map(u => ({ email: u.email, password: u.password }));
    }
}

export const authService = new AuthService(); 