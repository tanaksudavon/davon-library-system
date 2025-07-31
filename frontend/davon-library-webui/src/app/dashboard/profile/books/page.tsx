'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/lib/store/auth-store';
import { FiSearch, FiBookOpen, FiClock, FiCalendar, FiAlertCircle } from 'react-icons/fi';
import { Book, Loan, Reservation, LoanStatus, BookStatus } from '@/lib/api/types';
import { userProfileService } from '@/lib/api/services/user-profile.service';
import { bookService } from '@/lib/api/services/book.service';
import ProfileTabs from '@/components/profile/ProfileTabs';

interface UserBook {
  book: Book;
  type: 'borrowed' | 'reserved';
  loan?: Loan;
  reservation?: Reservation;
  dueDate?: string;
  queuePosition?: number;
}

export default function UserBooksPage() {
    const router = useRouter();
    const { user } = useAuthStore();
    const [isLoading, setIsLoading] = useState(true);
    const [books, setBooks] = useState<UserBook[]>([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [error, setError] = useState<string | null>(null);
    
    // Filter books based on search term
    const filteredBooks = books.filter(userBook => 
        userBook.book.title.toLowerCase().includes(searchTerm.toLowerCase()) || 
        `${userBook.book.author.firstName} ${userBook.book.author.lastName}`.toLowerCase().includes(searchTerm.toLowerCase())
    );

    useEffect(() => {
        if (!user) {
            router.replace('/login');
            return;
        }
        loadUserBooks();
    }, [user, router]);

    const loadUserBooks = async () => {
        if (!user) return;
        
        setIsLoading(true);
        setError(null);
        
        try {
            const profileData = await userProfileService.getUserProfileData(Number(user.id));
            const userBooks: UserBook[] = [];

            // Add borrowed books
            for (const loan of profileData.currentLoans) {
                if (loan.book) {
                    userBooks.push({
                        book: loan.book,
                        type: 'borrowed',
                        loan,
                        dueDate: loan.dueDate,
                        queuePosition: undefined // Borrowed books don't have queue positions
                    });
                }
            }

            // Add reserved books
            for (const reservation of profileData.reservations) {
                if (reservation.book) {
                    const queuePosition = await userProfileService.getUserReservationPosition(Number(user.id), reservation.book.id);
                    userBooks.push({
                        book: reservation.book,
                        type: 'reserved',
                        reservation,
                        queuePosition: queuePosition || undefined
                    });
                }
            }

            setBooks(userBooks);
        } catch (err) {
            console.error('Failed to load user books:', err);
            setError('Failed to load your books. Please try again.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleReturnBook = async (bookId: number) => {
        try {
            setError(null);
            const result = await bookService.returnBook(bookId);
            console.log('Return successful:', result);
            await loadUserBooks(); // Refresh the list
        } catch (err: any) {
            console.error('Failed to return book:', err);
            const errorMessage = err?.response?.data?.error || err?.message || 'Failed to return book. Please try again.';
            setError(errorMessage);
        }
    };

    const handleBorrowBook = async (bookId: number) => {
        if (!user) return;
        
        try {
            setError(null);
            const result = await bookService.borrowBook(bookId, Number(user.id));
            console.log('Borrow successful:', result);
            await loadUserBooks(); // Refresh the list
        } catch (err: any) {
            console.error('Failed to borrow book:', err);
            const errorMessage = err?.response?.data?.error || err?.message || 'Failed to borrow book. Please try again.';
            setError(errorMessage);
        }
    };

    const handleReserveBook = async (bookId: number) => {
        if (!user) return;
        
        try {
            setError(null);
            const result = await bookService.reserveBook(bookId, Number(user.id));
            console.log('Reserve successful:', result);
            await loadUserBooks(); // Refresh the list
        } catch (err: any) {
            console.error('Failed to reserve book:', err);
            const errorMessage = err?.response?.data?.error || err?.message || 'Failed to reserve book. Please try again.';
            setError(errorMessage);
        }
    };

    const getStatusColor = (userBook: UserBook) => {
        if (userBook.type === 'borrowed') {
            const isOverdue = userBook.dueDate && new Date(userBook.dueDate) < new Date();
            return isOverdue ? 'bg-red-100 text-red-800' : 'bg-blue-100 text-blue-800';
        }
        return 'bg-yellow-100 text-yellow-800';
    };

    const getStatusText = (userBook: UserBook) => {
        if (userBook.type === 'borrowed') {
            const isOverdue = userBook.dueDate && new Date(userBook.dueDate) < new Date();
            return isOverdue ? 'Overdue' : 'Borrowed';
        }
        return `Reserved ${userBook.queuePosition ? `(#${userBook.queuePosition} in queue)` : ''}`;
    };

    if (!user) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <div className="text-center">
                    <h2 className="text-xl font-semibold">Authentication required</h2>
                    <p className="mt-2">Please log in to view your books</p>
                </div>
            </div>
        );
    }

    if (isLoading) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-500 mx-auto"></div>
                    <p className="mt-4 text-gray-600">Loading your books...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-4 py-8">
            <div className="max-w-4xl mx-auto">
                {/* Header */}
                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-gray-900">Profile</h1>
                    <p className="text-gray-600 mt-2">Manage your account settings and preferences</p>
                </div>

                {/* Profile Tabs */}
                <div className="bg-white rounded-lg shadow">
                    <ProfileTabs />

                    {/* Books Content */}
                    <div className="p-6">
                        <h2 className="text-2xl font-bold text-slate-800 mb-6">My Books</h2>
                        
                        {error && (
                            <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-md">
                                <div className="flex">
                                    <FiAlertCircle className="h-5 w-5 text-red-400 mt-0.5 mr-3" />
                                    <p className="text-red-700">{error}</p>
                                </div>
                            </div>
                        )}
                        
                        {/* Search Bar */}
                        <div className="relative mb-6">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <FiSearch className="h-5 w-5 text-gray-400" />
                            </div>
                            <input
                                type="text"
                                className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                placeholder="Search your books by title or author..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                        </div>
                        
                        {/* Books Grid */}
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                            {filteredBooks.length > 0 ? (
                                filteredBooks.map(userBook => (
                                    <div key={`${userBook.book.id}-${userBook.type}`} className="bg-white rounded-lg shadow-sm overflow-hidden border">
                                        <div className="h-48 bg-gray-200 overflow-hidden">
                                            {userBook.book.coverImage ? (
                                                <img
                                                    src={userBook.book.coverImage}
                                                    alt={userBook.book.title}
                                                    className="w-full h-full object-cover"
                                                />
                                            ) : (
                                                <div className="w-full h-full flex items-center justify-center">
                                                    <FiBookOpen className="h-16 w-16 text-gray-400" />
                                                </div>
                                            )}
                                        </div>
                                        
                                        <div className="p-4">
                                            <h3 className="font-semibold text-gray-900 mb-1 line-clamp-2">
                                                {userBook.book.title}
                                            </h3>
                                            <p className="text-sm text-gray-600 mb-2">
                                                by {userBook.book.author.firstName} {userBook.book.author.lastName}
                                            </p>
                                            
                                            {/* Status and Date Info */}
                                            <div className="mb-3">
                                                {userBook.type === 'borrowed' && (
                                                    <div className="flex items-center text-sm text-blue-600 mb-1">
                                                        <FiBookOpen className="mr-1 w-4 h-4" />
                                                        Currently Borrowed
                                                    </div>
                                                )}
                                                {userBook.type === 'reserved' && (
                                                    <div className="flex items-center text-sm text-yellow-600 mb-1">
                                                        <FiClock className="mr-1 w-4 h-4" />
                                                        Reserved
                                                    </div>
                                                )}
                                                
                                                {userBook.dueDate && (
                                                    <div className="flex items-center text-sm text-gray-600">
                                                        <FiCalendar className="mr-1 w-4 h-4" />
                                                        Due: {new Date(userBook.dueDate).toLocaleDateString()}
                                                    </div>
                                                )}
                                            </div>
                                            
                                            {/* Action Buttons */}
                                            <div className="space-y-2">
                                                {userBook.type === 'borrowed' && (
                                                    <button
                                                        onClick={() => handleReturnBook(userBook.book.id)}
                                                        className="w-full flex justify-center items-center py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none"
                                                    >
                                                        <FiBookOpen className="mr-2" /> Return Book
                                                    </button>
                                                )}

                                                {userBook.type === 'reserved' && (
                                                    <div className="w-full flex justify-center items-center py-2 px-4 border border-yellow-300 rounded-md shadow-sm text-sm font-medium text-yellow-700 bg-yellow-50">
                                                        <FiClock className="mr-2" /> 
                                                        {userBook.queuePosition ? `Position #${userBook.queuePosition}` : 'Reserved'}
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                ))
                            ) : (
                                <div className="col-span-full text-center py-10">
                                    <FiBookOpen className="mx-auto h-12 w-12 text-gray-400" />
                                    <h3 className="mt-2 text-sm font-medium text-gray-900">
                                        {searchTerm ? 'No matching books found' : 'No books in your library'}
                                    </h3>
                                    <p className="mt-1 text-sm text-gray-500">
                                        {searchTerm 
                                            ? 'Try adjusting your search terms.' 
                                            : 'Browse the library to find books to borrow or reserve.'
                                        }
                                    </p>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
} 