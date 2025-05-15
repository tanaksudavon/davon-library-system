import { User, UserLoginInput, AuthResponse } from '@/types/user';
import { getAllUsers, createUser } from './user-service';

class AuthService {
    async login(email: string, password: string): Promise<AuthResponse> {
        // Get all users from localStorage
        const users = getAllUsers();
        
        // Find user with matching email and password
        const user = users.find(u => u.email === email);
        
        if (!user || user.password !== password) {
            throw new Error('Invalid email or password');
        }
        
        // Generate a simple token (in a real app, this would be more secure)
        const token = btoa(user.email + ':' + new Date().getTime());
        
        // Store token in localStorage
        if (typeof window !== 'undefined') {
            localStorage.setItem('token', token);
            // Also store user object without password
            const { password: _, ...userWithoutPassword } = user;
            localStorage.setItem('user', JSON.stringify(userWithoutPassword));
        }
        
        // Return user info and token
        const { password: _, ...userWithoutPassword } = user;
        return {
            user: userWithoutPassword,
            token
        };
    }

    async register(username: string, email: string, password: string): Promise<AuthResponse> {
        // Check if user with this email or username already exists
        const users = getAllUsers();
        
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
        const newUser = createUser({
            username,
            email,
            password,
            role: 'user' // Default role for new registrations
        });
        
        // Generate token for automatic login
        const token = btoa(newUser.email + ':' + new Date().getTime());
        
        // Store token in localStorage
        if (typeof window !== 'undefined') {
            localStorage.setItem('token', token);
            // Also store user object without password
            const { password: _, ...userWithoutPassword } = newUser;
            localStorage.setItem('user', JSON.stringify(userWithoutPassword));
        }
        
        // Return user info without password and token
        const { password: _, ...userWithoutPassword } = newUser;
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
        try {
            // First try to get user directly from localStorage
            if (typeof window !== 'undefined') {
                const userJson = localStorage.getItem('user');
                if (userJson) {
                    const user = JSON.parse(userJson);
                    if (user && user.email) {
                        return user;
                    }
                }
            }
            
            // Fall back to token-based lookup if direct user lookup fails
            const token = this.getToken();
            if (!token) return null;
            
            // Decode token to get email
            const decodedToken = atob(token);
            const email = decodedToken.split(':')[0];
            
            // Find user with this email
            const users = getAllUsers();
            const user = users.find(u => u.email === email);
            
            if (!user) return null;
            
            // Store user in localStorage for future use
            const { password, ...userWithoutPassword } = user;
            if (typeof window !== 'undefined') {
                localStorage.setItem('user', JSON.stringify(userWithoutPassword));
            }
            
            return userWithoutPassword;
        } catch (error) {
            console.error('Error getting current user:', error);
            return null;
        }
    }
}

export const authService = new AuthService(); 