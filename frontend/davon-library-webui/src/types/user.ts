export interface User {
    id: string;
    username: string;
    email: string;
    password: string;
    role: 'admin' | 'user';
    createdAt: string;
    updatedAt: string;
}

export interface UserCreateInput {
    username: string;
    email: string;
    password: string;
    role?: 'admin' | 'user';
}

export interface UserUpdateInput {
    username?: string;
    email?: string;
    password?: string;
    role?: 'admin' | 'user';
}

export interface UserLoginInput {
    email: string;
    password: string;
}

export interface UserRegisterInput {
    username: string;
    email: string;
    password: string;
}

export interface AuthResponse {
    user: Omit<User, 'password'>;
    token: string;
} 