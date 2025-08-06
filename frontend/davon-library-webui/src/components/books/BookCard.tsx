'use client';

import { Book, BookStatus, LoanStatus } from '@/lib/api/types';
import { FiBookOpen, FiUser, FiCalendar, FiEdit, FiTrash2, FiHeart, FiX } from 'react-icons/fi';
import { useRouter } from 'next/navigation';
import { authService } from '@/lib/services/auth-service';
import { loanService } from '@/lib/api/services/loan.service';
import { favoriteService } from '@/lib/api/services/favorite.service';
import Button from '@/components/shared/Button';
import { useState, useEffect, useRef } from 'react';
import { useAuthStore } from '@/lib/store/auth-store';

interface BookCardProps {
  book: Book;
  isAdmin?: boolean;
  onEdit?: (book: Book) => void;
  onDelete?: (id: number) => void;
  onReturn?: (book: Book) => Promise<void>;
  isReservedByUser?: boolean;
  onCancelReservation?: (book: Book) => Promise<void>;
  isCurrentUserBorrower?: boolean;
}

export default function BookCard({ 
  book, 
  isAdmin = false, 
  onEdit, 
  onDelete, 
  onReturn, 
  isReservedByUser = false,
  onCancelReservation,
  isCurrentUserBorrower = false,
}: BookCardProps) {
  const router = useRouter();
  const currentUser = useAuthStore(state => state.user);
  const [loading, setLoading] = useState(true);
  const [isFavorited, setIsFavorited] = useState(false);
  const [favoriteLoading, setFavoriteLoading] = useState(false);
  const isCheckingRef = useRef(false);

  // Debug: Log current user info
  console.log(`[BookCard] Current user:`, currentUser);
  console.log(`[BookCard] isCurrentUserBorrower prop:`, isCurrentUserBorrower);

  // Check if current user has favorited this book
  useEffect(() => {
    const checkFavoriteStatus = async () => {
      if (!currentUser || !book) {
        setIsFavorited(false);
        setLoading(false);
        return;
      }

      if (isCheckingRef.current) {
        return;
      }

      isCheckingRef.current = true;
      setLoading(true);

      try {
        const userId = typeof currentUser.id === 'string' ? parseInt(currentUser.id) : currentUser.id;
        
        const favoriteStatus = await favoriteService.getFavoriteStatus(userId, book.id).catch(() => ({ isFavorited: false }));
        
        console.log(`[BookCard] Favorite status:`, favoriteStatus);
        
        setIsFavorited(favoriteStatus.isFavorited);
      } catch (error) {
        console.error('Failed to check favorite status:', error);
        setIsFavorited(false);
      } finally {
        setLoading(false);
        isCheckingRef.current = false;
      }
    };

    checkFavoriteStatus();
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
        if (isCurrentUserBorrower) {
          onReturn?.(book);
        } else if (isReservedByUser) {
          onCancelReservation?.(book);
        } else {
          handleReserveClick();
        }
        break;
      case BookStatus.RESERVED:
        if (isReservedByUser) {
          onCancelReservation?.(book);
        }
        break;
      default:
        break;
    }
  };

  const handleFavoriteToggle = async () => {
    if (!currentUser || favoriteLoading) return;

    setFavoriteLoading(true);
    try {
      const userId = typeof currentUser.id === 'string' ? parseInt(currentUser.id) : currentUser.id;
      const result = await favoriteService.toggleFavorite(userId, book.id);
      setIsFavorited(result.isFavorited);
    } catch (error) {
      console.error('Failed to toggle favorite:', error);
    } finally {
      setFavoriteLoading(false);
    }
  };

  const getClickableStatusText = (status: BookStatus) => {
    if (isAdmin) return getStatusText(status);
    
    switch (status) {
      case BookStatus.AVAILABLE:
        return '🔷 Available - Click to Borrow';
      case BookStatus.BORROWED:
        if (isCurrentUserBorrower) {
          return '📖 Borrowed - Click to Return';
        } else if (isReservedByUser) {
          return '📅 Borrowed - Reserved by you (Click to Cancel)';
        } else {
          return '📅 Borrowed - Click to Reserve';
        }
      case BookStatus.RESERVED:
        if (isReservedByUser) {
          return '📋 Reserved by you - Click to Cancel';
        }
        return '📋 Reserved';
      default:
        return getStatusText(status);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-200 overflow-hidden relative">
      {/* Book Cover */}
      <div className="h-48 bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center">
        <FiBookOpen className="text-6xl text-white opacity-80" />
      </div>

      {/* Book Details */}
      <div className="p-4">
        <div className="flex justify-between items-start mb-2">
          <h3 className="text-lg font-semibold text-gray-900 line-clamp-2 flex-1 pr-2">
            {book.title}
          </h3>
          {/* Status Button */}
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
              {/* If user currently has this book, show return button */}
              {isCurrentUserBorrower ? (
                <div className="pr-12"> {/* Add right padding to avoid heart overlap */}
                  <Button
                    variant="secondary"
                    size="sm"
                    onClick={() => onReturn?.(book)}
                    className="flex items-center w-full justify-center bg-green-500 hover:bg-green-600 text-white"
                  >
                    <FiBookOpen className="w-4 h-4 mr-1" />
                    Return Book
                  </Button>
                </div>
              ) : (
                <>
                  {/* Show loading state while checking */}
                  {loading ? (
                    <div className="w-full pr-12"> {/* Add right padding to avoid heart overlap */}
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
                    <div className="pr-12"> {/* Add right padding to avoid heart overlap */}
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
                      
                      {/* Borrowed books can be reserved by others or cancelled if reserved */}
                      {book.status === BookStatus.BORROWED && (
                        isReservedByUser ? (
                          <Button
                            variant="danger"
                            size="sm"
                            onClick={() => onCancelReservation?.(book)}
                            className="flex items-center w-full justify-center bg-red-500 text-white hover:bg-red-600"
                          >
                            <FiX className="w-4 h-4 mr-1" />
                            Cancel Reservation
                          </Button>
                        ) : (
                          <Button
                            variant="secondary"
                            size="sm"
                            onClick={handleReserveClick}
                            className="flex items-center w-full justify-center bg-orange-500 text-white hover:bg-orange-600"
                          >
                            <FiCalendar className="w-4 h-4 mr-1" />
                            Reserve
                          </Button>
                        )
                      )}

                      {/* Reserved books show status or cancel if by user */}
                      {book.status === BookStatus.RESERVED && (
                        isReservedByUser ? (
                          <Button
                            variant="danger"
                            size="sm"
                            onClick={() => onCancelReservation?.(book)}
                            className="flex items-center w-full justify-center bg-red-500 text-white hover:bg-red-600"
                          >
                            <FiX className="w-4 h-4 mr-1" />
                            Cancel Reservation
                          </Button>
                        ) : (
                          <Button
                            variant="secondary"
                            size="sm"
                            disabled
                            className="flex items-center w-full justify-center opacity-50"
                          >
                            <FiCalendar className="w-4 h-4 mr-1" />
                            Reserved
                          </Button>
                        )
                      )}
                    </div>
                  )}
                </>
              )}
            </>
          )}
        </div>
        
        {/* Heart/Favorite Button - Bottom Right Corner */}
        {!isAdmin && currentUser && (
          <button
            onClick={handleFavoriteToggle}
            disabled={favoriteLoading}
            className={`absolute bottom-3 right-3 p-2 rounded-full transition-all duration-200 hover:scale-110 z-10 shadow-md ${
              favoriteLoading 
                ? 'opacity-50 cursor-not-allowed' 
                : 'hover:shadow-lg cursor-pointer'
            } ${
              isFavorited 
                ? 'text-red-500 bg-white border-2 border-red-200 hover:bg-red-50' 
                : 'text-gray-400 bg-white border-2 border-gray-200 hover:text-red-400 hover:border-red-200'
            }`}
            title={isFavorited ? 'Remove from favorites' : 'Add to favorites'}
          >
            <FiHeart 
              className={`w-5 h-5 transition-all duration-200 ${
                isFavorited ? 'fill-current' : ''
              }`} 
            />
          </button>
        )}
      </div>
    </div>
  );
}