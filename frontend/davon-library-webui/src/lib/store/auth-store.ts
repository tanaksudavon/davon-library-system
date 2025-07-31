import { create } from 'zustand';
import { User } from '@/lib/api/types';

interface AuthState {
    user: User | null;
    token: string | null;
    setAuth: (user: User, token: string) => void;
    clearAuth: () => void;
}

// Helper to safely get user from localStorage
const getUserFromStorage = (): User | null => {
    if (typeof window === 'undefined') return null;
    
    try {
        const userJson = localStorage.getItem('user');
        return userJson ? JSON.parse(userJson) : null;
    } catch (error) {
        console.error('Error parsing user from localStorage:', error);
        return null;
    }
};

export const useAuthStore = create<AuthState>((set) => ({
    // Initialize user from localStorage if available
    user: typeof window !== 'undefined' ? getUserFromStorage() : null,
    token: typeof window !== 'undefined' ? localStorage.getItem('token') : null,
    
    setAuth: (user: User, token: string) => {
        if (typeof window !== 'undefined') {
            // Store both token and user object in localStorage
            localStorage.setItem('token', token);
            localStorage.setItem('user', JSON.stringify(user));
            console.log('[AuthStore] Saved user and token to localStorage');
        }
        set({ user, token });
    },
    
    clearAuth: () => {
        if (typeof window !== 'undefined') {
            // Clear both token and user from localStorage
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            localStorage.removeItem('login_success');
            localStorage.removeItem('redirecting_to_login');
            console.log('[AuthStore] Cleared auth from localStorage');
        }
        set({ user: null, token: null });
    },
})); 