// This file provides mock user data for development purposes only
// Remove or disable this in production
import { UserRole } from '@/types/user';

export const MOCK_ADMIN_USER = {
  id: "admin-123",
  username: "admin",
  email: "admin@davonlibrary.com",
  role: "admin" as UserRole,
  createdAt: new Date().toISOString(),
  updatedAt: new Date().toISOString()
};

export const MOCK_NORMAL_USER = {
  id: "user-123",
  username: "user",
  email: "user@user.com",
  role: "user" as UserRole,
  createdAt: new Date().toISOString(),
  updatedAt: new Date().toISOString()
}; 