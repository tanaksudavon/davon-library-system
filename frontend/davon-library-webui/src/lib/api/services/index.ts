// Export all API services
export { authService } from './auth.service';
export { bookService } from './book.service';
export { userService } from './user.service';
export { loanService } from './loan.service';

// Export types for convenience
export type {
  ApiError,
  NetworkError,
} from '../client';

export {
  isApiError,
  isNetworkError,
  getErrorMessage,
} from '../client';

// Export all types
export * from '../types';

// Export configuration
export { API_CONFIG } from '../config'; 