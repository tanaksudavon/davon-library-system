// API Configuration
export const API_CONFIG = {
  BASE_URL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8081',
  ENDPOINTS: {
    // Authentication
    AUTH: {
      LOGIN: '/api/auth/login',
      REGISTER: '/api/auth/register',
    },
    // Books
    BOOKS: {
      BASE: '/api/books',
      BY_ID: (id: number) => `/api/books/${id}`,
    },
    // Users
    USERS: {
      BASE: '/api/users',
      BY_ID: (id: number) => `/api/users/${id}`,
    },
    // Authors
    AUTHORS: {
      BASE: '/api/authors',
      BY_ID: (id: number) => `/api/authors/${id}`,
    },
    // Categories
    CATEGORIES: {
      BASE: '/api/categories',
      BY_ID: (id: number) => `/api/categories/${id}`,
    },
    // Loans
    LOANS: {
      BASE: '/api/loans',
      BY_ID: (id: number) => `/api/loans/${id}`,
      BY_USER: (userId: number) => `/api/loans/user/${userId}`,
    },
    // Reservations
    RESERVATIONS: {
      BASE: '/api/reservations',
      BY_ID: (id: number) => `/api/reservations/${id}`,
      BY_USER: (userId: number) => `/api/reservations/user/${userId}`,
    },
    // Fines
    FINES: {
      BASE: '/api/fines',
      BY_ID: (id: number) => `/api/fines/${id}`,
      BY_USER: (userId: number) => `/api/fines/user/${userId}`,
    },
    // Search
    SEARCH: {
      BOOKS: '/api/search/books',
      AUTHORS: '/api/search/authors',
      GLOBAL: '/api/search',
    },
  },
  HEADERS: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
  TIMEOUT: 10000, // 10 seconds
} as const;

export default API_CONFIG; 