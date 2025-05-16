import { User } from './user';

export interface AuthState {
    user: Omit<User, 'password'> | null;
    token: string | null;
    setAuth: (user: Omit<User, 'password'>, token: string) => void;
    clearAuth: () => void;
} 