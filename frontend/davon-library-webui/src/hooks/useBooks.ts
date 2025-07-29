'use client';

import { useState, useEffect, useCallback } from 'react';
import { useLibrary } from '@/contexts/LibraryContext';
import { Book, BookStatus } from '@/lib/api/types';
import { BookFilters } from '@/components/books/SearchAndFilter';

export function useBooks() {
  const { state, actions } = useLibrary();
  const [localFilters, setLocalFilters] = useState<BookFilters>({});

  // Book operations
  const {
    loadBooks,
    addBook,
    updateBook,
    deleteBook,
    selectBook,
    getFilteredBooks,
    getPaginatedBooks,
    getBookStats,
  } = actions;

  // Apply filters from SearchAndFilter component
  const applyFilters = useCallback((filters: BookFilters) => {
    setLocalFilters(filters);
    actions.setFilters({
      searchTerm: filters.category || '',
      category: filters.category || '',
      status: filters.status || '',
      author: filters.author || '',
    });
  }, [actions]);

  // Get books with advanced filtering
  const getAdvancedFilteredBooks = useCallback(() => {
    let filtered = getFilteredBooks();

    // Apply additional filters from SearchAndFilter
    if (localFilters.publishedAfter) {
      filtered = filtered.filter(book => 
        book.publishDate && new Date(book.publishDate) >= new Date(localFilters.publishedAfter!)
      );
    }

    if (localFilters.publishedBefore) {
      filtered = filtered.filter(book => 
        book.publishDate && new Date(book.publishDate) <= new Date(localFilters.publishedBefore!)
      );
    }

    return filtered;
  }, [getFilteredBooks, localFilters]);

  // Get unique categories for filter dropdown
  const getCategories = useCallback(() => {
    const categories = new Set(state.books.map(book => book.category));
    return Array.from(categories).sort();
  }, [state.books]);

  // Get unique authors for suggestions
  const getAuthors = useCallback(() => {
    const authors = new Set(state.books.map(book => book.author));
    return Array.from(authors).sort();
  }, [state.books]);

  // Search books with debouncing
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedSearchTerm, setDebouncedSearchTerm] = useState('');

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearchTerm(searchTerm);
    }, 300);

    return () => clearTimeout(timer);
  }, [searchTerm]);

  useEffect(() => {
    actions.setFilters({ searchTerm: debouncedSearchTerm });
  }, [debouncedSearchTerm, actions]);

  // Borrow/Return operations (mock implementation)
  const borrowBook = useCallback(async (book: Book) => {
    if (book.status !== BookStatus.AVAILABLE) {
      throw new Error('Book is not available for borrowing');
    }

    await updateBook(book.id, { status: BookStatus.BORROWED });
    actions.addActivity({
      action: 'Book Borrowed',
      details: `${book.title} borrowed`,
      type: 'borrow',
    });
  }, [updateBook, actions]);

  const returnBook = useCallback(async (book: Book) => {
    if (book.status !== BookStatus.BORROWED) {
      throw new Error('Book is not currently borrowed');
    }

    await updateBook(book.id, { status: BookStatus.AVAILABLE });
    actions.addActivity({
      action: 'Book Returned',
      details: `${book.title} returned`,
      type: 'return',
    });
  }, [updateBook, actions]);

  return {
    // State
    books: state.books,
    loading: state.loading.books,
    operationsLoading: state.loading.operations,
    error: state.error,
    selectedBook: state.selectedBook,
    filters: state.filters,
    pagination: state.pagination,
    
    // Actions
    loadBooks,
    addBook,
    updateBook,
    deleteBook,
    selectBook,
    borrowBook,
    returnBook,
    
    // Filtering
    applyFilters,
    getFilteredBooks: getAdvancedFilteredBooks,
    getPaginatedBooks,
    getCategories,
    getAuthors,
    
    // Search
    searchTerm,
    setSearchTerm,
    
    // Stats
    getBookStats,
    
    // Pagination
    setPagination: actions.setPagination,
  };
} 