export type UserRole = 'admin' | 'user';

export interface User {
    id: string;
    username: string;
    email: string;
    password: string;
    role: UserRole;
    createdAt: string;
    updatedAt: string;
}

export interface UserCreateInput {
    username: string;
    email: string;
    password: string;
    role?: UserRole;
}

export interface UserUpdateInput {
    username?: string;
    email?: string;
    password?: string;
    role?: UserRole;
}

export type UserLoginInput = {
    email: string;
    password: string;
};

export type AuthResponse = {
    user: Omit<User, 'password'>;
    token: string;
}; 