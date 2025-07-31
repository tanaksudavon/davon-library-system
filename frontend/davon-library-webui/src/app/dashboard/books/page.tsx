'use client';

import { useState, useEffect, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import { Book, BookStatus, UserRole, LoanStatus} from '@/lib/api/types';
import { bookService } from '@/lib/api/services/book.service';
import { loanService } from '@/lib/api/services/loan.service';
import { authService } from '@/lib/services/auth-service';
import { FiPlus, FiSearch, FiBookOpen } from 'react-icons/fi';
import BookFormModal from '@/components/books/BookFormModal';

export default function BooksPage() {
  const [books, setBooks] = useState<Book[]>([]);
  const [filteredBooks, setFilteredBooks] = useState<Book[]>([]);
  const [userLoans, setUserLoans] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [selectedStatus, setSelectedStatus] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingBook, setEditingBook] = useState<Book | null>(null);
  
  const currentUser = authService.getCurrentUser();
  const isAdmin = currentUser?.role === UserRole.LIBRARIAN;
  const router = useRouter();

  const loadBooks = useCallback(async () => {
    console.log('Loading books...');
    try {
      console.log('Making API call to:', 'http://localhost:8081/api/books');
      const allBooks = await bookService.getAllBooks();
      console.log('Books received:', allBooks);
      setBooks(allBooks);
      setFilteredBooks(allBooks); // Initialize filteredBooks with all books
    } catch (error) {
      console.error('Failed to load books:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  const loadUserLoans = useCallback(async () => {
    if (!currentUser) return;
    try {
      const loans = await loanService.getLoansByUserId(currentUser.id);
      setUserLoans(loans);
    } catch (error) {
      console.error('Failed to load user loans:', error);
    }
  }, [currentUser]);

  useEffect(() => {
    loadBooks();
    if (currentUser && !isAdmin) {
      loadUserLoans();
    }
  }, []); // Empty dependency array - only run once on mount

  const isCurrentUserBorrower = (bookId: number) => {
    return userLoans.some(loan => 
      loan.book.id === bookId && !loan.returnDate // Check for no return date (active loan)
    );
  };

  const handleReturn = async (book: Book) => {
    try {
      const result = await bookService.returnBook(book.id);
      console.log('Return successful:', result);
      
      // Extract the book from the result (in case it returns a Loan object)
      const updatedBook = 'book' in result ? result.book : result;
      
      setBooks(books.map(b => b.id === book.id ? updatedBook : b));
      setFilteredBooks(filteredBooks.map(b => b.id === book.id ? updatedBook : b));
    } catch (err: any) {
      console.error('Failed to return book:', err);
    }
  };

  const handleSaveBook = (savedBook: Book) => {
    if (editingBook) {
      // Update existing book in both books and filteredBooks
      setBooks(books.map(book => book.id === savedBook.id ? savedBook : book));
      setFilteredBooks(filteredBooks.map(book => book.id === savedBook.id ? savedBook : book));
    } else {
      // Add new book to both books and filteredBooks
      setBooks([...books, savedBook]);
      setFilteredBooks([...filteredBooks, savedBook]);
    }
    setShowModal(false);
    setEditingBook(null);
  };

  const handleEditBook = (book: Book) => {
    setEditingBook(book);
    setShowModal(true);
  };

  const handleAddBook = () => {
    setEditingBook(null);
    setShowModal(true);
  };

  const handleDeleteBook = async (id: number) => {
    try {
      await bookService.deleteBook(id);
      setBooks(books.filter(book => book.id !== id));
      setFilteredBooks(filteredBooks.filter(book => book.id !== id)); // Update filtered books
    } catch (error) {
      console.error('Failed to delete book:', error);
    }
  };

  // Filter books based on search term
  useEffect(() => {
    const newFilteredBooks = books.filter(book => 
      book.title.toLowerCase().includes(searchQuery.toLowerCase()) || 
      `${book.author.firstName} ${book.author.lastName}`.toLowerCase().includes(searchQuery.toLowerCase()) ||
      book.isbn.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setFilteredBooks(newFilteredBooks);
  }, [searchQuery, books]);

  const handleStatusClick = (book: Book) => {
    if (isAdmin) return; // Admins can't perform user actions
    
    switch (book.status) {
      case BookStatus.AVAILABLE:
        router.push(`/dashboard/borrow/${book.id}`);
        break;
      case BookStatus.BORROWED:
        if (isCurrentUserBorrower(book.id)) {
          handleReturn(book);
        } else {
          router.push(`/dashboard/reserve/${book.id}`);
        }
        break;
      case BookStatus.RESERVED:
        // Reserved books can't be clicked
        break;
      default:
        break;
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-500"></div>
      </div>
    );
  }
  
  return (
    <div className="space-y-6">
      {/* Header with action buttons for librarians */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Library Books</h1>
          <p className="text-gray-600">
            {isAdmin ? 'Manage your library collection' : 'Browse available books'}
          </p>
        </div>
        
        {isAdmin && (
          <div className="flex space-x-3">
            <button
              onClick={handleAddBook}
              className="flex items-center px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
            >
              <FiPlus className="w-4 h-4 mr-2" />
              Add Book
            </button>
          </div>
        )}
      </div>

      {/* Search Bar */}
      <div className="relative">
        <FiSearch className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
        <input
          type="text"
          placeholder={isAdmin ? "Search books by title, author, or ISBN..." : "Search books..."}
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
        />
      </div>

      {/* Books Display */}
      {loading ? (
        <div className="flex items-center justify-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-500"></div>
        </div>
      ) : (
        <>
          {/* Results Summary */}
          <div className="flex items-center justify-between">
            <p className="text-sm text-gray-600">
              Showing {filteredBooks.length} of {books.length} books
              {searchQuery && (
                <span>
                  {' '}for "<strong>{searchQuery}</strong>"
                </span>
              )}
            </p>
            {searchQuery && (
              <button
                onClick={() => setSearchQuery('')}
                className="text-primary-600 hover:text-primary-700 text-sm"
              >
                Clear search
              </button>
            )}
          </div>

          {/* Admin Table View */}
          {isAdmin ? (
            <div className="bg-white rounded-lg shadow overflow-hidden">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Book</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Author</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Category</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {filteredBooks.length > 0 ? (
                    filteredBooks.map(book => (
                      <tr key={book.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="flex items-center">
                            <div className="flex-shrink-0 h-10 w-10">
                              <div className="h-10 w-10 bg-primary-100 rounded-lg flex items-center justify-center">
                                <FiBookOpen className="h-5 w-5 text-primary-600" />
                              </div>
                            </div>
                            <div className="ml-4">
                              <div className="text-sm font-medium text-gray-900">{book.title}</div>
                              <div className="text-sm text-gray-500">{book.isbn}</div>
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {book.author.firstName} {book.author.lastName}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {book.category.name}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(book.status)}`}>
                            {getClickableStatusText(book.status, book.id)}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                          <button
                            onClick={() => handleEditBook(book)}
                            className="text-indigo-600 hover:text-indigo-900"
                          >
                            Edit
                          </button>
                          <button
                            onClick={() => handleDeleteBook(book.id)}
                            className="text-red-600 hover:text-red-900"
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan={5} className="px-6 py-12 text-center">
                        <FiBookOpen className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                        <h3 className="text-lg font-medium text-gray-900 mb-2">No books found</h3>
                        <p className="text-gray-600 mb-4">
                          {searchQuery ? 'Try adjusting your search terms.' : 'Get started by adding your first book.'}
                        </p>
                        <button
                          onClick={handleAddBook}
                          className="inline-flex items-center px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
                        >
                          <FiPlus className="w-4 h-4 mr-2" />
                          Add First Book
                        </button>
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          ) : (
            /* Normal User Card View with Clickable Status Badges */
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
              {filteredBooks.length > 0 ? (
                filteredBooks.map(book => (
                  <div key={book.id} className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-200 overflow-hidden">
                    {/* Book Cover */}
                    <div className="h-48 bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center">
                      <FiBookOpen className="text-6xl text-white opacity-80" />
                    </div>

                    {/* Book Details */}
                    <div className="p-4">
                      <div className="flex justify-between items-start mb-2">
                        <h3 className="text-lg font-semibold text-gray-900 line-clamp-2">
                          {book.title}
                        </h3>
                        <button
                          onClick={() => handleStatusClick(book)}
                          disabled={book.status === BookStatus.RESERVED}
                          className={`px-2 py-1 rounded-full text-xs font-medium transition-all duration-200 ${getStatusColor(book.status)} ${
                            book.status !== BookStatus.RESERVED 
                              ? 'hover:scale-105 hover:shadow-md cursor-pointer' 
                              : 'cursor-not-allowed opacity-75'
                          }`}
                          title={getClickableStatusText(book.status, book.id)}
                        >
                          {getClickableStatusText(book.status, book.id)}
                        </button>
                      </div>

                      <div className="space-y-2 mb-4">
                        <div className="flex items-center text-gray-600">
                          <FiBookOpen className="w-4 h-4 mr-2" />
                          <span className="text-sm">{`${book.author.firstName} ${book.author.lastName}`}</span>
                        </div>
                        
                        {book.isbn && (
                          <div className="flex items-center text-gray-600">
                            <FiBookOpen className="w-4 h-4 mr-2" />
                            <span className="text-sm">ISBN: {book.isbn}</span>
                          </div>
                        )}

                        {book.publishDate && (
                          <div className="flex items-center text-gray-600">
                            <FiSearch className="w-4 h-4 mr-2" />
                            <span className="text-sm">Published: {new Date(book.publishDate).getFullYear()}</span>
                          </div>
                        )}
                      </div>

                      {/* Description */}
                      {book.description && (
                        <p className="text-gray-600 text-sm mb-4 line-clamp-3">
                          {book.description}
                        </p>
                      )}

                      {/* Action Buttons */}
                      <div className="flex flex-wrap gap-2">
                        {/* Available books can be borrowed */}
                        {book.status === BookStatus.AVAILABLE && (
                          <button
                            onClick={() => router.push(`/dashboard/borrow/${book.id}`)}
                            className="flex items-center px-3 py-2 bg-primary-600 text-white text-sm rounded-lg hover:bg-primary-700 transition-colors"
                          >
                            <FiBookOpen className="w-4 h-4 mr-1" />
                            Borrow
                          </button>
                        )}
                        
                        {/* Borrowed books - return for borrower, reserve for others */}
                        {book.status === BookStatus.BORROWED && (
                          <>
                            {isCurrentUserBorrower(book.id) ? (
                              <button
                                onClick={() => handleReturn(book)}
                                className="flex items-center px-3 py-2 bg-green-600 text-white text-sm rounded-lg hover:bg-green-700 transition-colors"
                              >
                                <FiBookOpen className="w-4 h-4 mr-1" />
                                Return
                              </button>
                            ) : (
                              <button
                                onClick={() => router.push(`/dashboard/reserve/${book.id}`)}
                                className="flex items-center px-3 py-2 bg-orange-500 text-white text-sm rounded-lg hover:bg-orange-600 transition-colors"
                              >
                                <FiSearch className="w-4 h-4 mr-1" />
                                Reserve
                              </button>
                            )}
                          </>
                        )}

                        {/* Reserved books show status only */}
                        {book.status === BookStatus.RESERVED && (
                          <button
                            disabled
                            className="flex items-center px-3 py-2 bg-gray-300 text-gray-500 text-sm rounded-lg opacity-50 cursor-not-allowed"
                          >
                            <FiSearch className="w-4 h-4 mr-1" />
                            Reserved
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                ))
              ) : (
                <div className="col-span-full text-center py-12">
                  <FiBookOpen className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                  <h3 className="text-lg font-medium text-gray-900 mb-2">No books found</h3>
                  <p className="text-gray-600">
                    {searchQuery ? 'Try adjusting your search terms.' : 'No books are currently available.'}
                  </p>
                </div>
              )}
            </div>
          )}
        </>
      )}

      {/* Modals */}
      <BookFormModal
        isOpen={showModal}
        onClose={() => {
          setShowModal(false);
          setEditingBook(null);
        }}
        onSuccess={handleSaveBook}
        book={editingBook || undefined}
      />

      {/* UserSearchModal was removed as per edit hint */}
    </div>
  );

  // Helper functions
  function getStatusColor(status: BookStatus): string {
    switch (status) {
      case BookStatus.AVAILABLE:
        return 'bg-green-100 text-green-800';
      case BookStatus.BORROWED:
        return 'bg-blue-100 text-blue-800';
      case BookStatus.RESERVED:
        return 'bg-yellow-100 text-yellow-800';
      case BookStatus.MAINTENANCE:
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  function getStatusText(status: BookStatus): string {
    switch (status) {
      case BookStatus.AVAILABLE:
        return 'Available';
      case BookStatus.BORROWED:
        return 'Borrowed';
      case BookStatus.RESERVED:
        return 'Reserved';
      case BookStatus.MAINTENANCE:
        return 'Maintenance';
      default:
        return status;
    }
  }

  function getClickableStatusText(status: BookStatus, bookId: number): string {
    if (isAdmin) return getStatusText(status);
    
    switch (status) {
      case BookStatus.AVAILABLE:
        return '🔷 Available - Click to Borrow';
      case BookStatus.BORROWED:
        return isCurrentUserBorrower(bookId) ? '📖 Borrowed - Click to Return' : '📅 Borrowed - Click to Reserve';
      case BookStatus.RESERVED:
        return '📋 Reserved';
      default:
        return getStatusText(status);
    }
  }
} 