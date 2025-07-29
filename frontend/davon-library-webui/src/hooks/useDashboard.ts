'use client';

import { useCallback, useMemo } from 'react';
import { useLibrary } from '@/contexts/LibraryContext';
import { useAuthStore } from '@/lib/store/auth-store';

export function useDashboard() {
  const { state, actions, dispatch } = useLibrary();
  const { user } = useAuthStore();

  // Get comprehensive dashboard statistics
  const getStats = useCallback(() => {
    const bookStats = actions.getBookStats();
    const users = state.users;
    
    return {
      books: {
        total: bookStats.total,
        available: bookStats.available,
        borrowed: bookStats.borrowed,
        maintenance: bookStats.maintenance,
        borrowRate: bookStats.total > 0 ? ((bookStats.borrowed / bookStats.total) * 100).toFixed(1) : '0',
        availabilityRate: bookStats.total > 0 ? ((bookStats.available / bookStats.total) * 100).toFixed(1) : '0',
      },
      users: {
        total: users.length,
        admins: users.filter(u => u.role === 'LIBRARIAN').length,
        regularUsers: users.filter(u => u.role === 'MEMBER').length,
        recentlyJoined: users.filter(u => {
          if (!u.createdAt) return false;
          const joinDate = new Date(u.createdAt);
          const weekAgo = new Date();
          weekAgo.setDate(weekAgo.getDate() - 7);
          return joinDate >= weekAgo;
        }).length,
      },
      activities: {
        total: state.recentActivities.length,
        today: state.recentActivities.filter(activity => {
          const activityDate = new Date(activity.time);
          const today = new Date();
          return activityDate.toDateString() === today.toDateString();
        }).length,
      },
    };
  }, [state.users, state.recentActivities, actions]);

  // Get recent activities with filtering
  const getRecentActivities = useCallback((limit: number = 10, type?: string) => {
    let activities = [...state.recentActivities];
    
    if (type) {
      activities = activities.filter(activity => activity.type === type);
    }
    
    return activities.slice(0, limit);
  }, [state.recentActivities]);

  // Get trending data (most borrowed books, active users, etc.)
  const getTrendingData = useCallback(() => {
    // Most borrowed books (mock implementation - in real app would track borrows)
    const booksByCategory = state.books.reduce((acc, book) => {
      acc[book.category.name] = (acc[book.category.name] || 0) + 1;
      return acc;
    }, {} as Record<string, number>);

    const topCategories = Object.entries(booksByCategory)
      .sort(([, a], [, b]) => b - a)
      .slice(0, 5)
      .map(([category, count]) => ({ category, count }));

    // Most active time periods (mock data)
    const activityByHour = state.recentActivities.reduce((acc, activity) => {
      const hour = new Date(activity.time).getHours();
      acc[hour] = (acc[hour] || 0) + 1;
      return acc;
    }, {} as Record<number, number>);

    const peakHours = Object.entries(activityByHour)
      .sort(([, a], [, b]) => b - a)
      .slice(0, 3)
      .map(([hour, count]) => ({ hour: parseInt(hour), count }));

    return {
      topCategories,
      peakHours,
    };
  }, [state.books, state.recentActivities]);

  // Get user-specific dashboard data
  const getUserDashboard = useCallback(() => {
    if (!user) return null;

    // For regular users, show their borrowed books and history
    if (user.role === 'user') {
      // Mock user's borrowed books (in real app, would filter by user ID)
      const userBooks = state.books.filter(book => book.status === 'BORROWED').slice(0, 3);
      
      // Mock user's activity history
      const userActivities = state.recentActivities
        .filter(activity => activity.type === 'borrow' || activity.type === 'return')
        .slice(0, 5);

      return {
        borrowedBooks: userBooks,
        recentActivity: userActivities,
        stats: {
          currentlyBorrowed: userBooks.length,
          totalBorrowed: userActivities.filter(a => a.type === 'borrow').length,
          totalReturned: userActivities.filter(a => a.type === 'return').length,
        },
      };
    }

    // For admins, return full admin dashboard
    return {
      isAdmin: true,
      stats: getStats(),
      recentActivities: getRecentActivities(10),
      trending: getTrendingData(),
    };
  }, [user, state.books, state.recentActivities, getStats, getRecentActivities, getTrendingData]);

  // Quick actions for dashboard
  const quickActions = useMemo(() => {
    if (!user || user.role !== 'admin') return [];

    return [
      {
        id: 'add-book',
        title: 'Add New Book',
        description: 'Add a new book to the library',
        icon: 'book',
        color: 'blue',
        action: () => {
          // This would typically open a modal or navigate to add book page
          console.log('Add book action');
        },
      },
      {
        id: 'add-user',
        title: 'Register User',
        description: 'Register a new library member',
        icon: 'user',
        color: 'green',
        action: () => {
          console.log('Add user action');
        },
      },
      {
        id: 'view-reports',
        title: 'View Reports',
        description: 'Generate and view library reports',
        icon: 'chart',
        color: 'purple',
        action: () => {
          console.log('View reports action');
        },
      },
      {
        id: 'manage-categories',
        title: 'Manage Categories',
        description: 'Organize book categories',
        icon: 'folder',
        color: 'orange',
        action: () => {
          console.log('Manage categories action');
        },
      },
    ];
  }, [user]);

  // Error handling
  const clearError = useCallback(() => {
    dispatch({ type: 'SET_ERROR', payload: null });
  }, [dispatch]);

  // Refresh all dashboard data
  const refreshDashboard = useCallback(async () => {
    try {
      await Promise.all([
        actions.loadBooks(),
        actions.loadUsers(),
      ]);
    } catch (error) {
      console.error('Failed to refresh dashboard:', error);
    }
  }, [actions]);

  return {
    // State
    loading: state.loading.books || state.loading.users,
    error: state.error,
    user,

    // Data
    stats: getStats(),
    recentActivities: state.recentActivities,
    userDashboard: getUserDashboard(),
    quickActions,
    trending: getTrendingData(),

    // Actions
    getRecentActivities,
    addActivity: actions.addActivity,
    clearError,
    refreshDashboard,

    // Utilities
    isAdmin: user?.role === 'admin',
    isUser: user?.role === 'user',
  };
} 