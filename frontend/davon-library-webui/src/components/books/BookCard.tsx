'use client';

import { Book, BookStatus, LoanStatus } from '@/lib/api/types';
import { FiBookOpen, FiUser, FiCalendar, FiEdit, FiTrash2 } from 'react-icons/fi';
import { useRouter } from 'next/navigation';
import { authService } from '@/lib/services/auth-service';
import { loanService } from '@/lib/api/services/loan.service';
import Button from '@/components/shared/Button';
import { useState, useEffect, useRef } from 'react';
import { useAuthStore } from '@/lib/store/auth-store';

interface BookCardProps {
  book: Book;
  isAdmin?: boolean;
  onEdit?: (book: Book) => void;
  onDelete?: (id: number) => void;
  onReturn?: (book: Book) => Promise<void>;
}

export default function BookCard({ 
  book, 
  isAdmin = false, 
  onEdit, 
  onDelete, 
  onReturn 
}: BookCardProps) {
  const router = useRouter();
  const currentUser = useAuthStore(state => state.user);
  const [isCurrentUserBorrower, setIsCurrentUserBorrower] = useState(false);
  const [loading, setLoading] = useState(true);
  const isCheckingRef = useRef(false);

  // Debug: Log current user info
  console.log(`[BookCard] Current user:`, currentUser);

  // Check if current user has borrowed this book
  useEffect(() => {
    const checkBorrowerStatus = async () => {
      if (!currentUser || !book) {
        setIsCurrentUserBorrower(false);
        setLoading(false);
        return;
      }

      // Prevent multiple simultaneous calls
      if (isCheckingRef.current) {
        return;
      }

      isCheckingRef.current = true;
      setLoading(true);

      try {
        const userId = typeof currentUser.id === 'string' ? parseInt(currentUser.id) : currentUser.id;
        
        console.log(`[BookCard] Checking if user ${userId} (${currentUser.username || currentUser.firstName}) has book ${book.id} (${book.title})`);
        
        const userLoans = await loanService.getLoansByUserId(userId);
        
        console.log(`[BookCard] User ${userId} has ${userLoans.length} total loans:`, userLoans);
        
        // Check if user has an active loan (no return date) for this book
        console.log(`[BookCard] Looking for active loans for book ID: ${book.id} (type: ${typeof book.id})`);
        
        const activeLoan = userLoans.find(
          loan => {
            console.log(`[BookCard] Checking loan ${loan.id}:`);
            console.log(`  - loan.book.id: ${loan.book.id} (type: ${typeof loan.book.id})`);
            console.log(`  - book.id: ${book.id} (type: ${typeof book.id})`);
            console.log(`  - returnDate: ${loan.returnDate}`);
            
            const hasBook = loan.book.id === book.id;
            const noReturnDate = !loan.returnDate;
            
            console.log(`  - hasBook: ${hasBook}, noReturnDate: ${noReturnDate}, active: ${hasBook && noReturnDate}`);
            
            return hasBook && noReturnDate;
          }
        );
        
        console.log(`[BookCard] Active loan found for book ${book.id}:`, activeLoan);
        console.log(`[BookCard] Setting isCurrentUserBorrower to:`, !!activeLoan);
        
        setIsCurrentUserBorrower(!!activeLoan);
      } catch (error) {
        console.error('Failed to check loan status:', error);
        setIsCurrentUserBorrower(false);
      } finally {
        setLoading(false);
        isCheckingRef.current = false;
      }
    };

    checkBorrowerStatus();
  }, [currentUser?.id, book.id]); // Only depend on user ID and book ID, not full objects

  const getStatusColor = (status: BookStatus) => {
    switch (status) {
      case BookStatus.AVAILABLE:
        return 'bg-green-100 text-green-800';
      case BookStatus.BORROWED:
        return 'bg-yellow-100 text-yellow-800';
      case BookStatus.RESERVED:
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusText = (status: BookStatus) => {
    switch (status) {
      case BookStatus.AVAILABLE:
        return 'Available';
      case BookStatus.BORROWED:
        return 'Borrowed';
      case BookStatus.RESERVED:
        return 'Reserved';
      default:
        return 'Unknown';
    }
  };

  const handleBorrowClick = () => {
    router.push(`/dashboard/borrow/${book.id}`);
  };

  const handleReserveClick = () => {
    router.push(`/dashboard/reserve/${book.id}`);
  };

  const handleStatusClick = () => {
    switch (book.status) {
      case BookStatus.AVAILABLE:
        handleBorrowClick();
        break;
      case BookStatus.BORROWED:
        // If current user is borrower, allow return, otherwise allow reserve
        if (isCurrentUserBorrower) {
          onReturn?.(book);
        } else {
          handleReserveClick();
        }
        break;
      case BookStatus.RESERVED:
        // Reserved books can't be clicked for actions by regular users
        if (!isAdmin) {
          return;
        }
        break;
      default:
        break;
    }
  };

  const getClickableStatusText = (status: BookStatus) => {
    if (isAdmin) return getStatusText(status);
    
    switch (status) {
      case BookStatus.AVAILABLE:
        return '🔷 Available - Click to Borrow';
      case BookStatus.BORROWED:
        return isCurrentUserBorrower ? '📖 Borrowed - Click to Return' : '📅 Borrowed - Click to Reserve';
      case BookStatus.RESERVED:
        return '📋 Reserved';
      default:
        return getStatusText(status);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-200 overflow-hidden">
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
            onClick={handleStatusClick}
            disabled={isAdmin || book.status === BookStatus.RESERVED || loading}
            className={`px-2 py-1 rounded-full text-xs font-medium transition-all duration-200 ${getStatusColor(book.status)} ${
              !isAdmin && book.status !== BookStatus.RESERVED && !loading
                ? 'hover:scale-105 hover:shadow-md cursor-pointer' 
                : isAdmin 
                  ? 'cursor-default' 
                  : 'cursor-not-allowed opacity-75'
            }`}
            title={!isAdmin ? getClickableStatusText(book.status) : getStatusText(book.status)}
          >
            {loading ? '...' : (!isAdmin ? getClickableStatusText(book.status) : getStatusText(book.status))}
          </button>
        </div>

        <div className="space-y-2 mb-4">
          <div className="flex items-center text-gray-600">
            <FiUser className="w-4 h-4 mr-2" />
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
              <FiCalendar className="w-4 h-4 mr-2" />
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
          {isAdmin && (
            <>
              <Button
                variant="secondary"
                size="sm"
                onClick={() => onEdit?.(book)}
                className="flex items-center"
              >
                <FiEdit className="w-4 h-4 mr-1" />
                Edit
              </Button>
              <Button
                variant="danger"
                size="sm"
                onClick={() => onDelete?.(book.id)}
                className="flex items-center"
              >
                <FiTrash2 className="w-4 h-4 mr-1" />
                Delete
              </Button>
            </>
          )}

          {!isAdmin && (
            <>
              {/* If user currently has this book, show message and return button only */}
              {isCurrentUserBorrower ? (
                <div className="space-y-2 w-full">
                  <div className="text-sm font-medium text-blue-700 bg-blue-100 p-3 rounded-lg border border-blue-200 text-center">
                    📚 You currently have this book
                  </div>
                  <Button
                    variant="secondary"
                    size="sm"
                    onClick={() => onReturn?.(book)}
                    className="flex items-center w-full justify-center"
                  >
                    <FiBookOpen className="w-4 h-4 mr-1" />
                    Return Book
                  </Button>
                </div>
              ) : (
                <>
                  {/* Show loading state while checking */}
                  {loading ? (
                    <div className="w-full">
                      <Button
                        variant="secondary"
                        size="sm"
                        disabled
                        className="flex items-center w-full justify-center opacity-50"
                      >
                        <FiBookOpen className="w-4 h-4 mr-1" />
                        Checking availability...
                      </Button>
                    </div>
                  ) : (
                    <>
                      {/* Available books can be borrowed */}
                      {book.status === BookStatus.AVAILABLE && (
                        <Button
                          variant="primary"
                          size="sm"
                          onClick={handleBorrowClick}
                          className="flex items-center w-full justify-center"
                        >
                          <FiBookOpen className="w-4 h-4 mr-1" />
                          Borrow
                        </Button>
                      )}
                      
                      {/* Borrowed books can be reserved by others (but not by current borrower) */}
                      {book.status === BookStatus.BORROWED && (
                        <Button
                          variant="secondary"
                          size="sm"
                          onClick={handleReserveClick}
                          className="flex items-center w-full justify-center bg-orange-500 text-white hover:bg-orange-600"
                        >
                          <FiCalendar className="w-4 h-4 mr-1" />
                          Reserve
                        </Button>
                      )}

                      {/* Reserved books show status only */}
                      {book.status === BookStatus.RESERVED && (
                        <div className="w-full">
                          <Button
                            variant="secondary"
                            size="sm"
                            disabled
                            className="flex items-center w-full justify-center opacity-50"
                          >
                            <FiCalendar className="w-4 h-4 mr-1" />
                            Reserved
                          </Button>
                        </div>
                      )}
                    </>
                  )}
                </>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
} 