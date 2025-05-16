import { User, UserCreateInput, UserUpdateInput } from '@/types/user';
import { v4 as uuidv4 } from 'uuid';

const USERS_KEY = 'davon_library_users';
const DEFAULT_ADMIN_PASSWORD = 'admin123';

// Initialize users if not exists
const initializeUsers = () => {
    if (typeof window === 'undefined') return;
    
    let users = [];
    
    try {
        const existingUsers = localStorage.getItem(USERS_KEY);
        if (existingUsers) {
            users = JSON.parse(existingUsers);
        }
    } catch (error) {
        console.error('[UserService] Error parsing users from localStorage:', error);
        users = [];
    }
    
    if (users.length === 0) {
        // Add default admin user
        const defaultAdmin: User = {
            id: uuidv4(),
            username: 'admin',
            email: 'admin@davonlibrary.com',
            password: DEFAULT_ADMIN_PASSWORD,
            role: 'admin',
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
        };
        
        // Add default regular user
        const defaultUser: User = {
            id: uuidv4(),
            username: 'user',
            email: 'user@user.com',
            password: '1234',
            role: 'user',
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
        };
        
        users.push(defaultAdmin, defaultUser);
        localStorage.setItem(USERS_KEY, JSON.stringify(users));
        console.log('[UserService] Default users created');
    }
};

// Get all users
export const getAllUsers = (): User[] => {
    if (typeof window === 'undefined') return [];
    console.log('[UserService] Getting all users...');
    
    initializeUsers();
    console.log('[UserService] Users initialized.');
    const users = localStorage.getItem(USERS_KEY);
    console.log('[UserService] All users from localStorage after init:', users);
    const parsedUsers = users ? JSON.parse(users) : [];
    console.log('[UserService] Parsed users:', parsedUsers);
    return parsedUsers;
};

// Get user by ID
export const getUserById = (id: string): User | null => {
    if (typeof window === 'undefined') return null;
    
    const users = getAllUsers();
    return users.find(user => user.id === id) || null;
};

// Create new user
export const createUser = (input: UserCreateInput): User => {
    if (typeof window === 'undefined') 
        throw new Error('Cannot create user on server side');
    
    const users = getAllUsers();
    const newUser: User = {
        id: uuidv4(),
        ...input,
        password: input.password,
        role: input.role || 'user',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
    };
    
    users.push(newUser);
    localStorage.setItem(USERS_KEY, JSON.stringify(users));
    return newUser;
};

// Update user
export const updateUser = (id: string, input: UserUpdateInput): User => {
    if (typeof window === 'undefined') 
        throw new Error('Cannot update user on server side');
    
    const users = getAllUsers();
    const userIndex = users.findIndex(user => user.id === id);
    
    if (userIndex === -1) {
        throw new Error('User not found');
    }
    
    const updatedUser = {
        ...users[userIndex],
        ...input,
        updatedAt: new Date().toISOString(),
    };
    
    users[userIndex] = updatedUser;
    localStorage.setItem(USERS_KEY, JSON.stringify(users));
    return updatedUser;
};

// Delete user
export const deleteUser = (id: string): void => {
    if (typeof window === 'undefined') 
        throw new Error('Cannot delete user on server side');
    
    const users = getAllUsers();
    const filteredUsers = users.filter(user => user.id !== id);
    localStorage.setItem(USERS_KEY, JSON.stringify(filteredUsers));
};

class UserService {
    // Get all users
    getAllUsers = (): User[] => {
        if (typeof window === 'undefined') return [];
        console.log('[UserService] Getting all users...');
        
        initializeUsers();
        console.log('[UserService] Users initialized.');
        const users = localStorage.getItem(USERS_KEY);
        console.log('[UserService] All users from localStorage after init:', users);
        const parsedUsers = users ? JSON.parse(users) : [];
        console.log('[UserService] Parsed users:', parsedUsers);
        return parsedUsers;
    };

    // Get user by ID
    getUserById = (id: string): User | null => {
        if (typeof window === 'undefined') return null;
        
        const users = this.getAllUsers();
        return users.find(user => user.id === id) || null;
    };

    // Create new user
    createUser = (input: UserCreateInput): User => {
        if (typeof window === 'undefined') 
            throw new Error('Cannot create user on server side');
        
        const users = this.getAllUsers();
        const newUser: User = {
            id: uuidv4(),
            ...input,
            password: input.password,
            role: input.role || 'user',
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
        };
        
        users.push(newUser);
        localStorage.setItem(USERS_KEY, JSON.stringify(users));
        return newUser;
    };

    // Update user
    updateUser = (id: string, input: UserUpdateInput): User => {
        if (typeof window === 'undefined') 
            throw new Error('Cannot update user on server side');
        
        const users = this.getAllUsers();
        const userIndex = users.findIndex(user => user.id === id);
        
        if (userIndex === -1) {
            throw new Error('User not found');
        }
        
        const updatedUser = {
            ...users[userIndex],
            ...input,
            updatedAt: new Date().toISOString(),
        };
        
        users[userIndex] = updatedUser;
        localStorage.setItem(USERS_KEY, JSON.stringify(users));
        return updatedUser;
    };

    // Delete user
    deleteUser = (id: string): void => {
        if (typeof window === 'undefined') 
            throw new Error('Cannot delete user on server side');
        
        const users = this.getAllUsers();
        const filteredUsers = users.filter(user => user.id !== id);
        localStorage.setItem(USERS_KEY, JSON.stringify(filteredUsers));
    };
}

export const userService = new UserService();

// Also keep existing individual exports if they are used elsewhere, or refactor to only use the service.
// For now, I'll keep them to minimize breaking changes if other parts of the app use them directly.
export {
    initializeUsers, 
    getAllUsers as _getAllUsersInternal,
    getUserById as _getUserByIdInternal, 
    createUser as _createUserInternal, 
    updateUser as _updateUserInternal, 
    deleteUser as _deleteUserInternal 
}; 