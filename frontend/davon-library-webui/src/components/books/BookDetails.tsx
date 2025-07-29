'use client';

import { Book, BookStatus } from '@/lib/api/types';
import { FiBookOpen, FiUser, FiCalendar, FiTag, FiFileText, FiEdit, FiTrash2 } from 'react-icons/fi';
import Button from '@/components/shared/Button';

interface BookDetailsProps {
  book: Book;
  isAdmin?: boolean;
  onEdit?: (book: Book) => void;
  onDelete?: (id: number) => Promise<void>;
  onBorrow?: (book: Book) => Promise<void>;
  onReturn?: (book: Book) => Promise<void>;
  onClose?: () => void;
}

export default function BookDetails({ 
  book, 
  isAdmin = false, 
  onEdit, 
  onDelete, 
  onBorrow, 
  onReturn,
  onClose 
}: BookDetailsProps) {
  const getStatusColor = (status: BookStatus) => {
    switch (status) {
      case BookStatus.AVAILABLE:
        return 'bg-green-100 text-green-800';
      case BookStatus.BORROWED:
        return 'bg-yellow-100 text-yellow-800';
      case BookStatus.MAINTENANCE:
        return 'bg-red-100 text-red-800';
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
      case BookStatus.MAINTENANCE:
        return 'Under Maintenance';
      case BookStatus.RESERVED:
        return 'Reserved';
      default:
        return 'Unknown';
    }
  };

  return (
    <div className="max-w-4xl mx-auto bg-white rounded-lg shadow-lg overflow-hidden">
      <div className="md:flex">
        {/* Book Cover */}
        <div className="md:w-1/3">
          <div className="h-96 bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center">
            {book.coverImage ? (
              <img 
                src={book.coverImage} 
                alt={book.title}
                className="w-full h-full object-cover"
              />
            ) : (
              <FiBookOpen className="text-8xl text-white opacity-80" />
            )}
          </div>
        </div>

        {/* Book Information */}
        <div className="md:w-2/3 p-8">
          <div className="flex justify-between items-start mb-4">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 mb-2">
                {book.title}
              </h1>
              <div className="flex items-center text-gray-600 mb-4">
                <FiUser className="w-5 h-5 mr-2" />
                <span className="text-lg">by {book.author.firstName} {book.author.lastName}</span>
              </div>
            </div>
            <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(book.status)}`}>
              {getStatusText(book.status)}
            </span>
          </div>

          {/* Book Details Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
            <div className="space-y-4">
              <div className="flex items-center">
                <FiBookOpen className="w-5 h-5 mr-3 text-gray-400" />
                <div>
                  <p className="text-sm text-gray-500">ISBN</p>
                  <p className="font-medium">{book.isbn}</p>
                </div>
              </div>

              <div className="flex items-center">
                <FiTag className="w-5 h-5 mr-3 text-gray-400" />
                <div>
                  <p className="text-sm text-gray-500">Category</p>
                  <p className="font-medium">{book.category.name}</p>
                </div>
              </div>
            </div>

            <div className="space-y-4">
              <div className="flex items-center">
                <FiCalendar className="w-5 h-5 mr-3 text-gray-400" />
                <div>
                  <p className="text-sm text-gray-500">Published</p>
                  <p className="font-medium">
                    {book.publishDate ? new Date(book.publishDate).toLocaleDateString() : 'N/A'}
                  </p>
                </div>
              </div>

              <div className="flex items-center">
                <FiFileText className="w-5 h-5 mr-3 text-gray-400" />
                <div>
                  <p className="text-sm text-gray-500">Added</p>
                  <p className="font-medium">
                    {book.createdAt ? new Date(book.createdAt).toLocaleDateString() : 'N/A'}
                  </p>
                </div>
              </div>
            </div>
          </div>

          {/* Description */}
          {book.description && (
            <div className="mb-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Description</h3>
              <p className="text-gray-600 leading-relaxed">
                {book.description}
              </p>
            </div>
          )}

          {/* Action Buttons */}
          <div className="flex flex-wrap gap-3">
            {isAdmin && (
              <>
                <Button
                  variant="secondary"
                  onClick={() => onEdit?.(book)}
                  className="flex items-center"
                >
                  <FiEdit className="w-4 h-4 mr-2" />
                  Edit Book
                </Button>
                <Button
                  variant="danger"
                  onClick={() => onDelete?.(book.id)}
                  className="flex items-center"
                >
                  <FiTrash2 className="w-4 h-4 mr-2" />
                  Delete Book
                </Button>
              </>
            )}

            {!isAdmin && (
              <>
                {book.status === BookStatus.AVAILABLE && (
                  <Button
                    variant="primary"
                    onClick={() => onBorrow?.(book)}
                    className="flex items-center"
                  >
                    <FiBookOpen className="w-4 h-4 mr-2" />
                    Borrow Book
                  </Button>
                )}
                
                {book.status === BookStatus.BORROWED && (
                  <Button
                    variant="secondary"
                    onClick={() => onReturn?.(book)}
                    className="flex items-center"
                  >
                    <FiBookOpen className="w-4 h-4 mr-2" />
                    Return Book
                  </Button>
                )}
              </>
            )}

            {onClose && (
              <Button
                variant="secondary"
                onClick={onClose}
              >
                Close
              </Button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
} 