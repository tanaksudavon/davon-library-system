// API Services - Single source of truth for all backend integrations
// These services provide typed interfaces to your backend REST APIs

export { userService } from './user.service';
export { bookService } from './book.service';
export { authService } from './auth.service';
export { loanService } from './loan.service';
export { searchService } from './search.service';
export { categoryService } from './category.service';
export { authorService } from './author.service';
export { reservationService } from './reservation.service';
export { fineService } from './fine.service';

// Export useful types that we know exist
export type { 
  CategoryCreateRequest, 
  CategoryUpdateRequest 
} from './category.service';
export type { 
  AuthorCreateRequest, 
  AuthorUpdateRequest 
} from './author.service';
export type { 
  ReservationCreateRequest
} from './reservation.service';
export type { 
  FineCreateRequest, 
  FineUpdateRequest 
} from './fine.service';

// Export types for convenience
export * from '../types'; 