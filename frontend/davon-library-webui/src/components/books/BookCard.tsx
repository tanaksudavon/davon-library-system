'use client';

import { Book, BookStatus } from '@/lib/api/types';
import { FiBookOpen, FiUser, FiCalendar, FiEdit, FiTrash2 } from 'react-icons/fi';
import Button from '@/components/shared/Button';

interface BookCardProps {
  book: Book;
  isAdmin?: boolean;
  onEdit?: (book: Book) => void;
  onDelete?: (id: number) => void;
  onBorrow?: (book: Book) => Promise<void>;
  onReturn?: (book: Book) => Promise<void>;
}

export default function BookCard({ 
  book, 
  isAdmin = false, 
  onEdit, 
  onDelete, 
  onBorrow, 
  onReturn 
}: BookCardProps) {
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
          <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(book.status)}`}>
            {getStatusText(book.status)}
          </span>
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
              {book.status === BookStatus.AVAILABLE && (
                <Button
                  variant="primary"
                  size="sm"
                  onClick={() => onBorrow?.(book)}
                  className="flex items-center"
                >
                  <FiBookOpen className="w-4 h-4 mr-1" />
                  Borrow
                </Button>
              )}
              
              {book.status === BookStatus.BORROWED && (
                <Button
                  variant="secondary"
                  size="sm"
                  onClick={() => onReturn?.(book)}
                  className="flex items-center"
                >
                  <FiBookOpen className="w-4 h-4 mr-1" />
                  Return
                </Button>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
} 