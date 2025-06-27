import { User } from '@/types/user';
import { v4 as uuidv4 } from 'uuid';

// Mock admin user for testing and development
export const MOCK_ADMIN_USER: User = {
    id: '1',
    username: 'admin',
    email: 'admin@davonlibrary.com',
    password: 'admin123',
    role: 'admin',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
};

// Mock regular user for testing and development
export const MOCK_NORMAL_USER: User = {
    id: '2',
    username: 'user',
    email: 'user@example.com',
    password: 'password123',
    role: 'user',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
}; 