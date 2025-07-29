# API Integration Guide

This document explains how the React frontend connects to the Java backend API.

## Architecture Overview

The frontend uses a layered architecture for API communication:

```
Components
    ↓
Custom Hooks (useBooks, useUsers, etc.)
    ↓
Context API (LibraryContext)
    ↓
API Services (bookService, userService, etc.)
    ↓
HTTP Client (httpClient)
    ↓
Java Backend (localhost:8081)
```

## Configuration

### Environment Variables

Create a `.env.local` file in the frontend root:

```bash
# API Configuration
NEXT_PUBLIC_API_URL=http://localhost:8081

# Development Settings
NODE_ENV=development

# Optional: Enable API debugging
NEXT_PUBLIC_API_DEBUG=true
```

### Backend Configuration

Ensure your Java backend is running on port 8081 with the following endpoints:

- `GET /api/books` - Get all books
- `POST /api/books` - Create book
- `GET /api/books/{id}` - Get book by ID
- `PUT /api/books/{id}` - Update book
- `DELETE /api/books/{id}` - Delete book

- `GET /api/users` - Get all users
- `POST /api/users` - Create user
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

- `GET /api/loans` - Get all loans
- `POST /api/loans` - Create loan
- `GET /api/loans/user/{userId}` - Get loans by user

## API Services

### Authentication Service

```typescript
import { authService } from '@/lib/api/services';

// Login
const user = await authService.login({
  username: 'john@example.com',
  password: 'password123'
});

// Register
const newUser = await authService.register({
  username: 'john_doe',
  email: 'john@example.com',
  password: 'password123',
  firstName: 'John',
  lastName: 'Doe'
});

// Check authentication
if (authService.isAuthenticated()) {
  const currentUser = authService.getCurrentUser();
}
```

### Book Service

```typescript
import { bookService } from '@/lib/api/services';

// Get all books
const books = await bookService.getAllBooks();

// Create book
const newBook = await bookService.createBook({
  title: 'New Book',
  isbn: '978-1234567890',
  description: 'Book description',
  status: BookStatus.AVAILABLE,
  authorId: 1,
  categoryId: 1
});

// Search books
const searchResults = await bookService.searchBooks({
  title: 'gatsby',
  author: 'fitzgerald'
});
```

### User Service

```typescript
import { userService } from '@/lib/api/services';

// Get all users
const users = await userService.getAllUsers();

// Create user
const newUser = await userService.createUser({
  username: 'jane_doe',
  email: 'jane@example.com',
  password: 'password123',
  firstName: 'Jane',
  lastName: 'Doe',
  role: UserRole.MEMBER
});
```

### Loan Service

```typescript
import { loanService } from '@/lib/api/services';

// Create loan (checkout book)
const loan = await loanService.createLoan({
  bookId: 1,
  userId: 2,
  dueDate: '2024-02-15'
});

// Return book
await loanService.returnBook(loanId);

// Get user's loans
const userLoans = await loanService.getLoansByUserId(userId);
```

## Error Handling

The API client provides comprehensive error handling:

```typescript
import { 
  bookService, 
  isApiError, 
  isNetworkError, 
  getErrorMessage 
} from '@/lib/api/services';

try {
  const books = await bookService.getAllBooks();
} catch (error) {
  if (isApiError(error)) {
    console.error('API Error:', error.status, error.message);
    if (error.status === 401) {
      // Handle unauthorized
      authService.logout();
      router.push('/login');
    }
  } else if (isNetworkError(error)) {
    console.error('Network Error:', error.message);
    // Show network error message
  } else {
    console.error('Unknown Error:', getErrorMessage(error));
  }
}
```

## Type Safety

All API interactions are fully typed with TypeScript interfaces that match your Java backend models:

```typescript
interface Book {
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

interface User {
  id: number;
  username: string;
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
```

## Usage in Components

### Using with React Hooks

```typescript
import { useState, useEffect } from 'react';
import { bookService } from '@/lib/api/services';

function BookList() {
  const [books, setBooks] = useState<Book[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadBooks() {
      try {
        setLoading(true);
        const data = await bookService.getAllBooks();
        setBooks(data);
      } catch (err) {
        setError(getErrorMessage(err));
      } finally {
        setLoading(false);
      }
    }

    loadBooks();
  }, []);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      {books.map(book => (
        <div key={book.id}>{book.title}</div>
      ))}
    </div>
  );
}
```

### Using with Context

```typescript
import { useLibrary } from '@/contexts/LibraryContext';

function BookComponent() {
  const { state, actions } = useLibrary();
  
  const handleAddBook = async (bookData) => {
    try {
      await actions.addBook(bookData);
      // Book automatically added to global state
    } catch (error) {
      console.error('Failed to add book:', error);
    }
  };

  return (
    <div>
      {state.books.map(book => (
        <div key={book.id}>{book.title}</div>
      ))}
    </div>
  );
}
```

## Testing the Integration

### 1. Start the Backend

```bash
cd backend/davon-library-backend
mvn quarkus:dev
```

The backend should be running on `http://localhost:8081`

### 2. Start the Frontend

```bash
cd frontend/davon-library-webui
npm run dev
```

The frontend should be running on `http://localhost:3000`

### 3. Test API Endpoints

You can test the API endpoints directly:

```bash
# Test get all books
curl http://localhost:8081/api/books

# Test login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## Troubleshooting

### Common Issues

1. **CORS Errors**
   - Ensure your Java backend has CORS configured
   - Add `quarkus.http.cors=true` to `application.properties`

2. **Connection Refused**
   - Check if backend is running on port 8081
   - Verify the API_URL environment variable

3. **Authentication Issues**
   - Check if login endpoint returns user data
   - Verify token storage in localStorage

4. **Type Errors**
   - Ensure backend model fields match TypeScript interfaces
   - Check enum values match between Java and TypeScript

### Debug Mode

Enable debug mode by setting:

```bash
NEXT_PUBLIC_API_DEBUG=true
```

This will log all API requests and responses to the console.

## Migration from Mock Data

The existing services have been updated to use the real API while maintaining backward compatibility:

- `authService` - Now connects to `/api/auth/*` endpoints
- `bookService` - Now connects to `/api/books/*` endpoints  
- `userService` - Now connects to `/api/users/*` endpoints

Components using these services should work without changes, but async methods are recommended for better error handling.

## Next Steps

1. Test all API endpoints with your Java backend
2. Add proper error handling to components
3. Implement loading states for better UX
4. Add form validation for create/update operations
5. Consider adding API caching for better performance 