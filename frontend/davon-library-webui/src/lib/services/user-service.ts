import { User } from '@/types/user';
import { v4 as uuidv4 } from 'uuid';
import { MOCK_ADMIN_USER, MOCK_NORMAL_USER } from './mockUser';

interface CreateUserParams {
    username: string;
    email: string;
    password: string;
    role?: 'admin' | 'user';
}

class UserService {
    constructor() {
        if (typeof window !== 'undefined') {
            // Initialize users in localStorage if not already present
            const users = this.getAllUsers();
            if (users.length === 0) {
                localStorage.setItem('users', JSON.stringify([
                    MOCK_ADMIN_USER,
                    MOCK_NORMAL_USER
                ]));
            }
        }
    }

    getAllUsers(): User[] {
        if (typeof window === 'undefined') return [];
        
        const usersJson = localStorage.getItem('users');
        if (!usersJson) return [];
        
        try {
            return JSON.parse(usersJson);
        } catch (error) {
            console.error('Error parsing users from localStorage:', error);
            return [];
        }
    }

    getUserById(id: string): User | null {
        const users = this.getAllUsers();
        return users.find(user => user.id === id) || null;
    }

    getUserByEmail(email: string): User | null {
        const users = this.getAllUsers();
        return users.find(user => user.email === email) || null;
    }

    createUser(params: CreateUserParams): User {
        const { username, email, password, role = 'user' } = params;
        
        // Create new user object
        const newUser: User = {
            id: uuidv4(),
            username,
            email,
            password,
            role,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
        };
        
        // Get current users and add the new one
        const currentUsers = this.getAllUsers();
        const updatedUsers = [...currentUsers, newUser];
        
        // Save to localStorage
        localStorage.setItem('users', JSON.stringify(updatedUsers));
        
        return newUser;
    }

    updateUser(id: string, updateData: Partial<Omit<User, 'id'>>): User | null {
        const users = this.getAllUsers();
        const userIndex = users.findIndex(u => u.id === id);
        
        if (userIndex === -1) return null;
        
        // Update user data
        const updatedUser = {
            ...users[userIndex],
            ...updateData,
            updatedAt: new Date().toISOString()
        };
        
        users[userIndex] = updatedUser;
        
        // Save to localStorage
        localStorage.setItem('users', JSON.stringify(users));
        
        return updatedUser;
    }

    deleteUser(id: string): boolean {
        const users = this.getAllUsers();
        const filteredUsers = users.filter(u => u.id !== id);
        
        if (filteredUsers.length === users.length) {
            // No user was removed
            return false;
        }
        
        // Save to localStorage
        localStorage.setItem('users', JSON.stringify(filteredUsers));
        
        return true;
    }
}

export const userService = new UserService(); 