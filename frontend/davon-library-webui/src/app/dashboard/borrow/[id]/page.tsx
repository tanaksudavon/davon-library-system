'use client';

import { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { bookService } from '@/lib/api/services/book.service';
import { Book, BookStatus } from '@/lib/api/types';
import { authService } from '@/lib/services/auth-service';
import { FiBookOpen, FiUser, FiCalendar, FiArrowLeft } from 'react-icons/fi';

export default function BorrowPage() {
  const params = useParams();
  const router = useRouter();
  const bookId = parseInt(params.id as string);
  
  const [book, setBook] = useState<Book | null>(null);
  const [loading, setLoading] = useState(true);
  const [borrowing, setBorrowing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [user, setUser] = useState<any>(null);

  useEffect(() => {
    const currentUser = authService.getCurrentUser();
    setUser(currentUser);
    loadBook();
  }, [bookId]);

  const loadBook = async () => {
    try {
      const bookData = await bookService.getBookById(bookId);
      setBook(bookData);
      setError(null);
    } catch (err) {
      setError('Failed to load book details');
      console.error('Error loading book:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleBorrow = async () => {
    if (!book || !user) return;

    setBorrowing(true);
    try {
      await bookService.borrowBook(book.id, user.id);
      // Success - redirect to books page
      router.push('/dashboard/books?borrowed=true');
    } catch (err: any) {
      // Only show error if the operation actually failed
      const errorMessage = err?.response?.data?.error || err?.message || 'Failed to borrow book. Please try again.';
      setError(errorMessage);
      console.error('Error borrowing book:', err);
    } finally {
      setBorrowing(false);
    }
  };

  const handleCancel = () => {
    router.back();
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-500"></div>
      </div>
    );
  }

  if (error || !book) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-red-600 mb-4">Error</h2>
          <p className="text-gray-600 mb-4">{error || 'Book not found'}</p>
          <button
            onClick={() => router.push('/dashboard/books')}
            className="bg-primary-500 text-white px-4 py-2 rounded-lg hover:bg-primary-600"
          >
            Back to Books
          </button>
        </div>
      </div>
    );
  }

  if (book.status !== BookStatus.AVAILABLE) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-yellow-600 mb-4">Book Not Available</h2>
          <p className="text-gray-600 mb-4">This book is currently {book.status.toLowerCase()}</p>
          <button
            onClick={() => router.push('/dashboard/books')}
            className="bg-primary-500 text-white px-4 py-2 rounded-lg hover:bg-primary-600"
          >
            Back to Books
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4">
        {/* Header */}
        <div className="flex items-center mb-6">
          <button
            onClick={handleCancel}
            className="flex items-center text-gray-600 hover:text-gray-800 mr-4"
          >
            <FiArrowLeft className="w-5 h-5 mr-1" />
            Back
          </button>
          <h1 className="text-3xl font-bold text-gray-900">Borrow Book</h1>
        </div>

        <div className="bg-white rounded-lg shadow-lg overflow-hidden">
          <div className="md:flex">
            {/* Book Cover */}
            <div className="md:w-1/3">
              <div className="h-96 bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center">
                <FiBookOpen className="text-8xl text-white opacity-80" />
              </div>
            </div>

            {/* Book Details */}
            <div className="md:w-2/3 p-8">
              <div className="mb-6">
                <h2 className="text-3xl font-bold text-gray-900 mb-2">{book.title}</h2>
                <div className="flex items-center text-gray-600 mb-2">
                  <FiUser className="w-5 h-5 mr-2" />
                  <span className="text-lg">by {book.author.firstName} {book.author.lastName}</span>
                </div>
                {book.publishDate && (
                  <div className="flex items-center text-gray-600 mb-4">
                    <FiCalendar className="w-5 h-5 mr-2" />
                    <span>Published: {new Date(book.publishDate).getFullYear()}</span>
                  </div>
                )}
                <div className="text-sm text-gray-500 space-y-1">
                  <p><strong>ISBN:</strong> {book.isbn}</p>
                  <p><strong>Category:</strong> {book.category.name}</p>
                </div>
              </div>

              {book.description && (
                <div className="mb-6">
                  <h3 className="text-lg font-semibold mb-2">Description</h3>
                  <p className="text-gray-700 leading-relaxed">{book.description}</p>
                </div>
              )}

              {/* Borrower Info */}
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
                <h3 className="text-lg font-semibold text-blue-900 mb-2">Borrowing Information</h3>
                <div className="text-sm text-blue-800 space-y-1">
                  <p><strong>Borrower:</strong> {user?.firstName} {user?.lastName}</p>
                  <p><strong>Email:</strong> {user?.email}</p>
                  <p><strong>Borrow Date:</strong> {new Date().toLocaleDateString()}</p>
                  <p><strong>Due Date:</strong> {new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toLocaleDateString()}</p>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex space-x-4">
                <button
                  onClick={handleBorrow}
                  disabled={borrowing}
                  className="flex-1 bg-primary-500 text-white py-3 px-6 rounded-lg hover:bg-primary-600 disabled:opacity-50 disabled:cursor-not-allowed font-semibold"
                >
                  {borrowing ? (
                    <>
                      <div className="animate-spin rounded-full h-4 w-4 border-t-2 border-white inline-block mr-2"></div>
                      Borrowing...
                    </>
                  ) : (
                    'Confirm Borrow'
                  )}
                </button>
                <button
                  onClick={handleCancel}
                  disabled={borrowing}
                  className="flex-1 bg-gray-300 text-gray-700 py-3 px-6 rounded-lg hover:bg-gray-400 disabled:opacity-50 font-semibold"
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
} 