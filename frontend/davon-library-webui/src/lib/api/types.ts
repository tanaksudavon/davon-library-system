// Enums matching Java backend
export enum BookStatus {
  AVAILABLE = 'AVAILABLE',
  BORROWED = 'BORROWED',
  MAINTENANCE = 'MAINTENANCE',
  RESERVED = 'RESERVED',
}

export enum UserRole {
  GUEST = 'GUEST',
  MEMBER = 'MEMBER',
  LIBRARIAN = 'LIBRARIAN',
}

export enum LoanStatus {
  ACTIVE = 'ACTIVE',
  RETURNED = 'RETURNED',
  OVERDUE = 'OVERDUE',
}

export enum ReservationStatus {
  ACTIVE = 'ACTIVE',
  FULFILLED = 'FULFILLED',
  CANCELLED = 'CANCELLED',
  EXPIRED = 'EXPIRED',
}

export enum FineStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  WAIVED = 'WAIVED',
}

// Core entity interfaces matching Java models
export interface Author {
  id: number;
  firstName: string;
  lastName: string;
  biography?: string;
  birthDate?: string;
  nationality?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Category {
  id: number;
  name: string;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Book {
  id: number;
  title: string;
  isbn: string;
  description?: string;
  publishDate?: string;
  coverImage?: string;
  status: BookStatus;
  author: Author;
  category: Category;
  createdAt?: string;
  updatedAt?: string;
}

export interface User {
  id: number;
  username: string;
  password?: string; // Optional for security
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  role: UserRole;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Loan {
  id: number;
  book: Book;
  user: User;
  borrowDate: string;
  returnDate?: string;
  dueDate: string;
  status: LoanStatus;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Reservation {
  id: number;
  book: Book;
  user: User;
  reservationDate: string;
  expirationDate: string;
  status: ReservationStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface Fine {
  id: number;
  user: User;
  loan?: Loan;
  amount: number;
  reason: string;
  status: FineStatus;
  dueDate?: string;
  paidDate?: string;
  createdAt?: string;
  updatedAt?: string;
}

// API Request/Response types
export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
}

export interface LoginResponse {
  user: User;
  token?: string; // If your backend implements JWT
}

// Create/Update DTOs (Data Transfer Objects)
export interface BookCreateRequest {
  title: string;
  isbn: string;
  description?: string;
  publishDate?: string;
  coverImage?: string;
  status: BookStatus;
  authorId: number;
  categoryId: number;
}

export interface BookUpdateRequest extends Partial<BookCreateRequest> {
  id: number;
}

export interface UserCreateRequest {
  username: string;
  password: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  role: UserRole;
}

export interface UserUpdateRequest extends Partial<UserCreateRequest> {
  id: number;
}

export interface LoanCreateRequest {
  bookId: number;
  userId: number;
  dueDate: string;
  notes?: string;
}

export interface ReservationCreateRequest {
  bookId: number;
  userId: number;
  expirationDate: string;
}

// API Response wrapper
export interface ApiResponse<T> {
  data: T;
  success: boolean;
  message?: string;
  error?: string;
}

// Pagination
export interface PaginatedResponse<T> {
  data: T[];
  currentPage: number;
  totalPages: number;
  totalItems: number;
  itemsPerPage: number;
}

// Search filters
export interface BookSearchFilters {
  title?: string;
  author?: string;
  category?: string;
  isbn?: string;
  status?: BookStatus;
  publishedAfter?: string;
  publishedBefore?: string;
}

export interface UserSearchFilters {
  username?: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  role?: UserRole;
  status?: string;
} 