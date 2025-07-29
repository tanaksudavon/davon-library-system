'use client';

import { useState, useEffect, useCallback } from 'react';
import { useLibrary } from '@/contexts/LibraryContext';
import { User, UserRole, UserCreateRequest } from '@/lib/api/types';

interface CreateUserParams {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role?: UserRole;
}

interface UserFilters {
  searchTerm: string;
  role: UserRole | '';
}

export function useUsers() {
  const { state, actions } = useLibrary();
  const [filters, setFilters] = useState<UserFilters>({
    searchTerm: '',
    role: '',
  });

  // User operations
  const { loadUsers, addUser, updateUser, deleteUser } = actions;

  // Search users with debouncing
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedSearchTerm, setDebouncedSearchTerm] = useState('');

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearchTerm(searchTerm);
    }, 300);

    return () => clearTimeout(timer);
  }, [searchTerm]);

  useEffect(() => {
    setFilters(prev => ({ ...prev, searchTerm: debouncedSearchTerm }));
  }, [debouncedSearchTerm]);

  // Get filtered users
  const getFilteredUsers = useCallback(() => {
    let filtered = [...state.users];

    // Apply search filter
    if (filters.searchTerm) {
      const searchLower = filters.searchTerm.toLowerCase();
      filtered = filtered.filter(user =>
        user.username.toLowerCase().includes(searchLower) ||
        user.email.toLowerCase().includes(searchLower)
      );
    }

    // Apply role filter
    if (filters.role) {
      filtered = filtered.filter(user => user.role === filters.role);
    }

    return filtered;
  }, [state.users, filters]);

  // Get user statistics
  const getUserStats = useCallback(() => {
    const users = state.users;
    return {
      total: users.length,
      admins: users.filter(user => user.role === UserRole.LIBRARIAN).length,
      regularUsers: users.filter(user => user.role === UserRole.MEMBER).length,
      recentlyJoined: users.filter(user => {
        if (!user.createdAt) return false;
        const joinDate = new Date(user.createdAt);
        const weekAgo = new Date();
        weekAgo.setDate(weekAgo.getDate() - 7);
        return joinDate >= weekAgo;
      }).length,
    };
  }, [state.users]);

  // User role management
  const promoteToAdmin = useCallback(async (userId: number) => {
    const user = state.users.find(u => u.id === userId);
    if (!user) {
      throw new Error('User not found');
    }

    if (user.role === UserRole.LIBRARIAN) {
      throw new Error('User is already an admin');
    }

    await updateUser(userId, { role: UserRole.LIBRARIAN });
    actions.addActivity({
      action: 'User Promoted',
      details: `${user.firstName} ${user.lastName} promoted to admin`,
      type: 'other',
    });
  }, [state.users, updateUser, actions]);

  const demoteFromAdmin = useCallback(async (userId: number) => {
    const user = state.users.find(u => u.id === userId);
    if (!user) {
      throw new Error('User not found');
    }

    if (user.role === UserRole.MEMBER) {
      throw new Error('User is not an admin');
    }

    await updateUser(userId, { role: UserRole.MEMBER });
    actions.addActivity({
      action: 'User Demoted',
      details: `${user.firstName} ${user.lastName} demoted from admin`,
      type: 'other',
    });
  }, [state.users, updateUser, actions]);

  // Create user with validation
  const createUser = useCallback(async (userData: CreateUserParams) => {
    // Check if email already exists
    const existingUser = state.users.find(u => u.email === userData.email);
    if (existingUser) {
      throw new Error('Email already exists');
    }

    // Check if username already exists
    const existingUsername = state.users.find(u => u.username === userData.username);
    if (existingUsername) {
      throw new Error('Username already exists');
    }

    const userCreateRequest: UserCreateRequest = {
      username: userData.username,
      email: userData.email,
      password: userData.password,
      firstName: userData.firstName,
      lastName: userData.lastName,
      role: userData.role || UserRole.MEMBER,
    };

    return await addUser(userCreateRequest);
  }, [state.users, addUser]);

  // Update user with validation
  const updateUserWithValidation = useCallback(async (userId: number, updates: Partial<User>) => {
    // If email is being updated, check for duplicates
    if (updates.email) {
      const existingUser = state.users.find(u => u.email === updates.email && u.id !== userId);
      if (existingUser) {
        throw new Error('Email already exists');
      }
    }

    // If username is being updated, check for duplicates
    if (updates.username) {
      const existingUser = state.users.find(u => u.username === updates.username && u.id !== userId);
      if (existingUser) {
        throw new Error('Username already exists');
      }
    }

    return await updateUser(userId, updates);
  }, [state.users, updateUser]);

  // Set role filter
  const setRoleFilter = useCallback((role: UserRole | '') => {
    setFilters(prev => ({ ...prev, role }));
  }, []);

  // Clear all filters
  const clearFilters = useCallback(() => {
    setFilters({
      searchTerm: '',
      role: '',
    });
    setSearchTerm('');
  }, []);

  return {
    // State
    users: state.users,
    loading: state.loading.users,
    operationsLoading: state.loading.operations,
    error: state.error,
    filters,

    // Actions
    loadUsers,
    createUser,
    updateUser: updateUserWithValidation,
    deleteUser,
    promoteToAdmin,
    demoteFromAdmin,

    // Filtering
    getFilteredUsers,
    setRoleFilter,
    clearFilters,

    // Search
    searchTerm,
    setSearchTerm,

    // Stats
    getUserStats,
  };
} 