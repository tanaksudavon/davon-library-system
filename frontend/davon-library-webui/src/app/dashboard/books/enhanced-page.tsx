'use client';

import { useState, useEffect } from 'react';
import { useBooks } from '@/hooks/useBooks';
import { useDashboard } from '@/hooks/useDashboard';
import { useLibrary } from '@/contexts/LibraryContext';
import BookCard from '@/components/books/BookCard';
import SearchAndFilter, { BookFilters } from '@/components/books/SearchAndFilter';
import BookFormModal from '@/components/books/BookFormModal';
import BookDetails from '@/components/books/BookDetails';
import Modal from '@/components/shared/Modal';
import Button from '@/components/shared/Button';
import Loader from '@/components/shared/Loader';
import { FiPlus, FiGrid, FiList, FiRefreshCw } from 'react-icons/fi';
import { Book } from '@/lib/api/types';

export default function EnhancedBooksPage() {
  // Use our custom hooks for state management
  const {
    books,
    loading,
    operationsLoading,
    error,
    selectedBook,
    searchTerm,
    setSearchTerm,
    getFilteredBooks,
    getPaginatedBooks,
    getCategories,
    borrowBook,
    returnBook,
    deleteBook,
    selectBook,
    applyFilters,
    setPagination,
    pagination,
  } = useBooks();

  const { isAdmin, addActivity } = useDashboard();
  const { actions: libraryActions } = useLibrary();

  // Local state for UI
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingBook, setEditingBook] = useState<Book | undefined>(undefined);
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
  const [showBookDetails, setShowBookDetails] = useState(false);

  // Handle search and filters
  const handleSearch = (term: string) => {
    setSearchTerm(term);
  };

  const handleFilter = (filters: BookFilters) => {
    applyFilters(filters);
  };

  const handleClear = () => {
    setSearchTerm('');
    applyFilters({});
  };

  // Book operations with activity tracking
  const handleAddBook = () => {
    setEditingBook(undefined);
    setIsModalOpen(true);
  };

  const handleEditBook = (book: Book) => {
    setEditingBook(book);
    setIsModalOpen(true);
  };

  const handleDeleteBook = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this book?')) {
      try {
        await deleteBook(Number(id));
        addActivity({
          action: 'Book Deleted',
          details: 'A book was removed from the library',
          type: 'other',
        });
      } catch (error) {
        console.error('Failed to delete book:', error);
      }
    }
  };

  const handleReturnBook = async (book: Book) => {
    try {
      await returnBook(book);
    } catch (error) {
      console.error('Failed to return book:', error);
      alert(error instanceof Error ? error.message : 'Failed to return book');
    }
  };

  const handleBookSelect = (book: Book) => {
    selectBook(book);
    setShowBookDetails(true);
  };

  // Pagination
  const handlePageChange = (page: number) => {
    setPagination({ currentPage: page });
  };

  // Get filtered and paginated books
  const filteredBooks = getFilteredBooks();
  const paginatedBooks = getPaginatedBooks();
  const totalPages = Math.ceil(filteredBooks.length / pagination.itemsPerPage);

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <Loader />
      </div>
    );
  }

  return (
    <div className="p-6">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Books</h1>
          <p className="text-gray-600 mt-1">
            {filteredBooks.length} of {books.length} books
          </p>
        </div>
        
        <div className="flex items-center gap-3">
          {/* View Mode Toggle */}
          <div className="flex bg-gray-100 rounded-lg p-1">
            <button
              onClick={() => setViewMode('grid')}
              className={`p-2 rounded-md transition-colors ${
                viewMode === 'grid'
                  ? 'bg-white text-blue-600 shadow-sm'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              <FiGrid className="w-4 h-4" />
            </button>
            <button
              onClick={() => setViewMode('list')}
              className={`p-2 rounded-md transition-colors ${
                viewMode === 'list'
                  ? 'bg-white text-blue-600 shadow-sm'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              <FiList className="w-4 h-4" />
            </button>
          </div>

          {/* Refresh Button */}
          <Button
            variant="secondary"
            onClick={() => libraryActions.loadBooks()}
            className="flex items-center"
            disabled={operationsLoading}
          >
            <FiRefreshCw className={`w-4 h-4 mr-2 ${operationsLoading ? 'animate-spin' : ''}`} />
            Refresh
          </Button>

          {/* Add Book Button (Admin only) */}
          {isAdmin && (
            <Button
              variant="primary"
              onClick={handleAddBook}
              className="flex items-center"
            >
              <FiPlus className="w-4 h-4 mr-2" />
              Add Book
            </Button>
          )}
        </div>
      </div>

      {/* Error Display */}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
          {error}
        </div>
      )}

      {/* Search and Filter */}
      <SearchAndFilter
        onSearch={handleSearch}
        onFilter={handleFilter}
        onClear={handleClear}
        searchTerm={searchTerm}
        categories={getCategories().map(category => category.name)}
      />

      {/* Books Display */}
      {paginatedBooks.length === 0 ? (
        <div className="text-center py-12">
          <div className="text-gray-400 text-6xl mb-4">📚</div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">No books found</h3>
          <p className="text-gray-600">
            {searchTerm || Object.values(getCategories()).some(Boolean)
              ? 'Try adjusting your search or filters'
              : 'Get started by adding your first book'}
          </p>
        </div>
      ) : (
        <>
          {/* Books Grid/List */}
          <div className={
            viewMode === 'grid'
              ? 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6'
              : 'space-y-4'
          }>
            {paginatedBooks.map((book) => (
              <div key={book.id} onClick={() => handleBookSelect(book)} className="cursor-pointer">
                <BookCard
                  book={book}
                  isAdmin={isAdmin}
                  onEdit={handleEditBook}
                  onDelete={handleDeleteBook}
                  onReturn={handleReturnBook}
                />
              </div>
            ))}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex justify-center items-center mt-8 space-x-2">
              <Button
                variant="secondary"
                onClick={() => handlePageChange(pagination.currentPage - 1)}
                disabled={pagination.currentPage === 1}
              >
                Previous
              </Button>
              
              {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                <Button
                  key={page}
                  variant={page === pagination.currentPage ? 'primary' : 'secondary'}
                  onClick={() => handlePageChange(page)}
                  className="w-10 h-10"
                >
                  {page}
                </Button>
              ))}
              
              <Button
                variant="secondary"
                onClick={() => handlePageChange(pagination.currentPage + 1)}
                disabled={pagination.currentPage === totalPages}
              >
                Next
              </Button>
            </div>
          )}
        </>
      )}

      {/* Book Form Modal */}
      <BookFormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        book={editingBook}
        onSuccess={() => {
          setIsModalOpen(false);
          setEditingBook(undefined);
        }}
      />

      {/* Book Details Modal */}
      {showBookDetails && selectedBook && (
        <Modal
          isOpen={showBookDetails}
          onClose={() => {
            setShowBookDetails(false);
            selectBook(null);
          }}
          title="Book Details"
          size="lg"
        >
          <BookDetails
            book={selectedBook}
            isAdmin={isAdmin}
            onEdit={handleEditBook}
            onDelete={handleDeleteBook}
            onReturn={handleReturnBook}
            onClose={() => {
              setShowBookDetails(false);
              selectBook(null);
            }}
          />
        </Modal>
      )}
    </div>
  );
} 