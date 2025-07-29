'use client';

import React, { createContext, useContext, useReducer, useEffect, ReactNode } from 'react';
import { Book, User, BookCreateRequest, BookStatus, UserCreateRequest } from '@/lib/api/types';
import { bookService } from '@/lib/api/services/book.service';
import { userService } from '@/lib/api/services/user.service';

// Types for our global state
interface LibraryState {
  books: Book[];
  users: User[];
  loading: {
    books: boolean;
    users: boolean;
    operations: boolean;
  };
  error: string | null;
  filters: {
    searchTerm: string;
    category: string;
    status: string;
    author: string;
  };
  pagination: {
    currentPage: number;
    itemsPerPage: number;
    totalItems: number;
  };
  selectedBook: Book | null;
  recentActivities: Activity[];
}

interface Activity {
  id: string;
  action: string;
  details: string;
  time: string;
  type: 'borrow' | 'return' | 'register' | 'add_book' | 'other';
}

interface CreateUserParams {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role?: 'LIBRARIAN' | 'MEMBER' | 'GUEST';
}

// Action types for our reducer
type LibraryAction =
  | { type: 'SET_LOADING'; payload: { key: keyof LibraryState['loading']; value: boolean } }
  | { type: 'SET_ERROR'; payload: string | null }
  | { type: 'SET_BOOKS'; payload: Book[] }
  | { type: 'ADD_BOOK'; payload: Book }
  | { type: 'UPDATE_BOOK'; payload: Book }
  | { type: 'DELETE_BOOK'; payload: number }
  | { type: 'SET_USERS'; payload: User[] }
  | { type: 'ADD_USER'; payload: User }
  | { type: 'UPDATE_USER'; payload: User }
  | { type: 'DELETE_USER'; payload: number }
  | { type: 'SET_FILTERS'; payload: Partial<LibraryState['filters']> }
  | { type: 'CLEAR_FILTERS' }
  | { type: 'SET_PAGINATION'; payload: Partial<LibraryState['pagination']> }
  | { type: 'SET_SELECTED_BOOK'; payload: Book | null }
  | { type: 'ADD_ACTIVITY'; payload: Activity }
  | { type: 'SET_ACTIVITIES'; payload: Activity[] };

// Initial state
const initialState: LibraryState = {
  books: [],
  users: [],
  loading: {
    books: false,
    users: false,
    operations: false,
  },
  error: null,
  filters: {
    searchTerm: '',
    category: '',
    status: '',
    author: '',
  },
  pagination: {
    currentPage: 1,
    itemsPerPage: 12,
    totalItems: 0,
  },
  selectedBook: null,
  recentActivities: [],
};

// Reducer function
function libraryReducer(state: LibraryState, action: LibraryAction): LibraryState {
  switch (action.type) {
    case 'SET_LOADING':
      return {
        ...state,
        loading: {
          ...state.loading,
          [action.payload.key]: action.payload.value,
        },
      };

    case 'SET_ERROR':
      return {
        ...state,
        error: action.payload,
      };

    case 'SET_BOOKS':
      return {
        ...state,
        books: action.payload,
        pagination: {
          ...state.pagination,
          totalItems: action.payload.length,
        },
      };

    case 'ADD_BOOK':
      const newBooks = [...state.books, action.payload];
      return {
        ...state,
        books: newBooks,
        pagination: {
          ...state.pagination,
          totalItems: newBooks.length,
        },
      };

    case 'UPDATE_BOOK':
      return {
        ...state,
        books: state.books.map(book =>
          book.id === action.payload.id ? action.payload : book
        ),
      };

    case 'DELETE_BOOK':
      const filteredBooks = state.books.filter(book => book.id !== action.payload);
      return {
        ...state,
        books: filteredBooks,
        pagination: {
          ...state.pagination,
          totalItems: filteredBooks.length,
        },
      };

    case 'SET_USERS':
      return {
        ...state,
        users: action.payload,
      };

    case 'ADD_USER':
      return {
        ...state,
        users: [...state.users, action.payload],
      };

    case 'UPDATE_USER':
      return {
        ...state,
        users: state.users.map(user =>
          user.id === action.payload.id ? action.payload : user
        ),
      };

    case 'DELETE_USER':
      return {
        ...state,
        users: state.users.filter(user => user.id !== action.payload),
      };

    case 'SET_FILTERS':
      return {
        ...state,
        filters: {
          ...state.filters,
          ...action.payload,
        },
        pagination: {
          ...state.pagination,
          currentPage: 1, // Reset to first page when filters change
        },
      };

    case 'CLEAR_FILTERS':
      return {
        ...state,
        filters: initialState.filters,
        pagination: {
          ...state.pagination,
          currentPage: 1,
        },
      };

    case 'SET_PAGINATION':
      return {
        ...state,
        pagination: {
          ...state.pagination,
          ...action.payload,
        },
      };

    case 'SET_SELECTED_BOOK':
      return {
        ...state,
        selectedBook: action.payload,
      };

    case 'ADD_ACTIVITY':
      return {
        ...state,
        recentActivities: [action.payload, ...state.recentActivities].slice(0, 50), // Keep only last 50 activities
      };

    case 'SET_ACTIVITIES':
      return {
        ...state,
        recentActivities: action.payload,
      };

    default:
      return state;
  }
}

// Context interface
interface LibraryContextType {
  state: LibraryState;
  dispatch: React.Dispatch<LibraryAction>;
  actions: {
    // Book actions
    loadBooks: () => Promise<void>;
    addBook: (book: BookCreateRequest) => Promise<Book>;
    updateBook: (id: number, updates: Partial<Book>) => Promise<Book>;
    deleteBook: (id: number) => Promise<void>;
    
    // User actions
    loadUsers: () => Promise<void>;
    addUser: (user: CreateUserParams) => Promise<User>;
    updateUser: (id: number, updates: Partial<User>) => Promise<User>;
    deleteUser: (id: number) => Promise<void>;
    
    // Filter actions
    setFilters: (filters: Partial<LibraryState['filters']>) => void;
    clearFilters: () => void;
    
    // Pagination actions
    setPagination: (pagination: Partial<LibraryState['pagination']>) => void;
    
    // Book selection
    selectBook: (book: Book | null) => void;
    
    // Activity tracking
    addActivity: (activity: Omit<Activity, 'id' | 'time'>) => void;
    
    // Utility functions
    getFilteredBooks: () => Book[];
    getPaginatedBooks: () => Book[];
    getBookStats: () => {
      total: number;
      available: number;
      borrowed: number;
      maintenance: number;
    };
  };
}

// Create the context
const LibraryContext = createContext<LibraryContextType | undefined>(undefined);

// Provider component
interface LibraryProviderProps {
  children: ReactNode;
}

export function LibraryProvider({ children }: LibraryProviderProps) {
  const [state, dispatch] = useReducer(libraryReducer, initialState);

  // Load initial data
  useEffect(() => {
    loadBooks();
    loadUsers();
    loadActivities();
  }, []);

  // Action implementations
  const loadBooks = async () => {
    dispatch({ type: 'SET_LOADING', payload: { key: 'books', value: true } });
    try {
      const books = await bookService.getAllBooks();
      dispatch({ type: 'SET_BOOKS', payload: books });
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: 'Failed to load books' });
    } finally {
      dispatch({ type: 'SET_LOADING', payload: { key: 'books', value: false } });
    }
  };

  const addBook = async (bookData: BookCreateRequest) => {
    dispatch({ type: 'SET_LOADING', payload: { key: 'operations', value: true } });
    try {
      const newBook = await bookService.createBook(bookData);
      dispatch({ type: 'ADD_BOOK', payload: newBook });
      
      // Add activity
      addActivity({
        action: 'New Book Added',
        details: `${newBook.title} by ${newBook.author.firstName} ${newBook.author.lastName}`,
        type: 'add_book',
      });
      
      return newBook;
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: 'Failed to add book' });
      throw error;
    } finally {
      dispatch({ type: 'SET_LOADING', payload: { key: 'operations', value: false } });
    }
  };

  const updateBook = async (id: number, updates: Partial<Book>) => {
    dispatch({ type: 'SET_LOADING', payload: { key: 'operations', value: true } });
    try {
      const updatedBook = await bookService.updateBook(id, updates as any);
      dispatch({ type: 'UPDATE_BOOK', payload: updatedBook });
      
      // Add activity
      addActivity({
        action: 'Book Updated',
        details: `${updatedBook.title} information updated`,
        type: 'other',
      });
      
      return updatedBook;
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: 'Failed to update book' });
      throw error;
    } finally {
      dispatch({ type: 'SET_LOADING', payload: { key: 'operations', value: false } });
    }
  };

  const deleteBook = async (id: number) => {
    dispatch({ type: 'SET_LOADING', payload: { key: 'operations', value: true } });
    try {
      const book = state.books.find(b => b.id === id);
      await bookService.deleteBook(id);
      dispatch({ type: 'DELETE_BOOK', payload: id });
      
      // Add activity
      if (book) {
        addActivity({
          action: 'Book Deleted',
          details: `${book.title} removed from library`,
          type: 'other',
        });
      }
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: 'Failed to delete book' });
      throw error;
    } finally {
      dispatch({ type: 'SET_LOADING', payload: { key: 'operations', value: false } });
    }
  };

  const loadUsers = async () => {
    dispatch({ type: 'SET_LOADING', payload: { key: 'users', value: true } });
    try {
      const users = await userService.getAllUsers();
      dispatch({ type: 'SET_USERS', payload: users });
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: 'Failed to load users' });
    } finally {
      dispatch({ type: 'SET_LOADING', payload: { key: 'users', value: false } });
    }
  };

  const addUser = async (userData: CreateUserParams) => {
    dispatch({ type: 'SET_LOADING', payload: { key: 'operations', value: true } });
    try {
      const userCreateRequest: UserCreateRequest = {
        username: userData.username,
        email: userData.email,
        password: userData.password,
        firstName: userData.firstName,
        lastName: userData.lastName,
        role: (userData.role as any) || 'MEMBER',
      };
      
      const newUser = await userService.createUser(userCreateRequest);
      dispatch({ type: 'ADD_USER', payload: newUser });
      
      // Add activity
      addActivity({
        action: 'New User Registered',
        details: `${newUser.username} joined the library`,
        type: 'register',
      });
      
      return newUser;
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: 'Failed to add user' });
      throw error;
    } finally {
      dispatch({ type: 'SET_LOADING', payload: { key: 'operations', value: false } });
    }
  };

  const updateUser = async (id: number, updates: Partial<User>) => {
    dispatch({ type: 'SET_LOADING', payload: { key: 'operations', value: true } });
    try {
      const updatedUser = await userService.updateUser(id, updates as any);
      if (!updatedUser) {
        throw new Error('User not found');
      }
      dispatch({ type: 'UPDATE_USER', payload: updatedUser });
      return updatedUser;
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: 'Failed to update user' });
      throw error;
    } finally {
      dispatch({ type: 'SET_LOADING', payload: { key: 'operations', value: false } });
    }
  };

  const deleteUser = async (id: number) => {
    dispatch({ type: 'SET_LOADING', payload: { key: 'operations', value: true } });
    try {
      await userService.deleteUser(id);
      dispatch({ type: 'DELETE_USER', payload: id });
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: 'Failed to delete user' });
      throw error;
    } finally {
      dispatch({ type: 'SET_LOADING', payload: { key: 'operations', value: false } });
    }
  };

  const setFilters = (filters: Partial<LibraryState['filters']>) => {
    dispatch({ type: 'SET_FILTERS', payload: filters });
  };

  const clearFilters = () => {
    dispatch({ type: 'CLEAR_FILTERS' });
  };

  const setPagination = (pagination: Partial<LibraryState['pagination']>) => {
    dispatch({ type: 'SET_PAGINATION', payload: pagination });
  };

  const selectBook = (book: Book | null) => {
    dispatch({ type: 'SET_SELECTED_BOOK', payload: book });
  };

  const addActivity = (activityData: Omit<Activity, 'id' | 'time'>) => {
    const activity: Activity = {
      ...activityData,
      id: Date.now().toString(),
      time: new Date().toISOString(),
    };
    dispatch({ type: 'ADD_ACTIVITY', payload: activity });
    
    // Persist to localStorage
    const activities = [activity, ...state.recentActivities].slice(0, 50);
    localStorage.setItem('library_activities', JSON.stringify(activities));
  };

  const loadActivities = () => {
    try {
      const stored = localStorage.getItem('library_activities');
      if (stored) {
        const activities = JSON.parse(stored);
        dispatch({ type: 'SET_ACTIVITIES', payload: activities });
      }
    } catch (error) {
      console.error('Failed to load activities:', error);
    }
  };

  const getFilteredBooks = () => {
    let filtered = [...state.books];
    
    // Apply search filter
    if (state.filters.searchTerm) {
      const searchLower = state.filters.searchTerm.toLowerCase();
      filtered = filtered.filter(book =>
        book.title.toLowerCase().includes(searchLower) ||
        `${book.author.firstName} ${book.author.lastName}`.toLowerCase().includes(searchLower) ||
        book.isbn.toLowerCase().includes(searchLower)
      );
    }
    
    // Apply category filter
    if (state.filters.category) {
      filtered = filtered.filter(book => book.category.name === state.filters.category);
    }
    
    // Apply status filter
    if (state.filters.status) {
      filtered = filtered.filter(book => book.status === state.filters.status);
    }
    
    // Apply author filter
    if (state.filters.author) {
      const authorLower = state.filters.author.toLowerCase();
      filtered = filtered.filter(book =>
        `${book.author.firstName} ${book.author.lastName}`.toLowerCase().includes(authorLower)
      );
    }
    
    return filtered;
  };

  const getPaginatedBooks = () => {
    const filtered = getFilteredBooks();
    const startIndex = (state.pagination.currentPage - 1) * state.pagination.itemsPerPage;
    const endIndex = startIndex + state.pagination.itemsPerPage;
    return filtered.slice(startIndex, endIndex);
  };

  const getBookStats = () => {
    const books = state.books;
    return {
      total: books.length,
      available: books.filter(book => book.status === BookStatus.AVAILABLE).length,
      borrowed: books.filter(book => book.status === BookStatus.BORROWED).length,
      maintenance: books.filter(book => book.status === BookStatus.MAINTENANCE).length,
    };
  };

  const contextValue: LibraryContextType = {
    state,
    dispatch,
    actions: {
      loadBooks,
      addBook,
      updateBook,
      deleteBook,
      loadUsers,
      addUser,
      updateUser,
      deleteUser,
      setFilters,
      clearFilters,
      setPagination,
      selectBook,
      addActivity,
      getFilteredBooks,
      getPaginatedBooks,
      getBookStats,
    },
  };

  return (
    <LibraryContext.Provider value={contextValue}>
      {children}
    </LibraryContext.Provider>
  );
}

// Custom hook to use the library context
export function useLibrary() {
  const context = useContext(LibraryContext);
  if (context === undefined) {
    throw new Error('useLibrary must be used within a LibraryProvider');
  }
  return context;
}

// Export types for use in components
export type { LibraryState, Activity }; 