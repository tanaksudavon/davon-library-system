'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/lib/store/auth-store';
import { FiSearch, FiBookOpen } from 'react-icons/fi';

export default function UserBooksPage() {
    const router = useRouter();
    const { user } = useAuthStore();
    const [isLoading, setIsLoading] = useState(false);
    
    // Sample books data (in a real app, this would come from an API)
    const [books, setBooks] = useState([
        { id: 1, title: 'To Kill a Mockingbird', author: 'Harper Lee', status: 'Available', coverUrl: 'https://picsum.photos/seed/book1/200/300' },
        { id: 2, title: '1984', author: 'George Orwell', status: 'Borrowed', dueDate: '2023-05-20', coverUrl: 'https://picsum.photos/seed/book2/200/300' },
        { id: 3, title: 'The Great Gatsby', author: 'F. Scott Fitzgerald', status: 'Available', coverUrl: 'https://picsum.photos/seed/book3/200/300' },
        { id: 4, title: 'Pride and Prejudice', author: 'Jane Austen', status: 'Available', coverUrl: 'https://picsum.photos/seed/book4/200/300' },
    ]);
    
    const [searchTerm, setSearchTerm] = useState('');
    
    // Filter books based on search term
    const filteredBooks = books.filter(book => 
        book.title.toLowerCase().includes(searchTerm.toLowerCase()) || 
        book.author.toLowerCase().includes(searchTerm.toLowerCase())
    );

    useEffect(() => {
        if (!user) {
            router.replace('/login');
        }
    }, [user, router]);

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

    return (
        <div className="container mx-auto">
            <h1 className="text-2xl font-bold text-slate-800 mb-6">My Books</h1>
            
            {/* Search Bar */}
            <div className="relative mb-6">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <FiSearch className="h-5 w-5 text-gray-400" />
                </div>
                <input
                    type="text"
                    className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                    placeholder="Search books by title or author..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
            </div>
            
            {/* Books Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                {filteredBooks.length > 0 ? (
                    filteredBooks.map(book => (
                        <div key={book.id} className="bg-white rounded-lg shadow-sm overflow-hidden">
                            <div className="h-48 bg-gray-200 overflow-hidden">
                                <img
                                    src={book.coverUrl}
                                    alt={`Cover of ${book.title}`}
                                    className="w-full h-full object-cover"
                                />
                            </div>
                            <div className="p-4">
                                <h3 className="text-lg font-semibold text-gray-800">{book.title}</h3>
                                <p className="text-sm text-gray-600 mb-2">by {book.author}</p>
                                
                                <div className="flex justify-between items-center">
                                    <span className={`text-xs px-2 py-1 rounded-full ${
                                        book.status === 'Available' 
                                            ? 'bg-green-100 text-green-800' 
                                            : 'bg-amber-100 text-amber-800'
                                    }`}>
                                        {book.status}
                                    </span>
                                    {book.dueDate && (
                                        <span className="text-xs text-gray-500">
                                            Due: {new Date(book.dueDate).toLocaleDateString()}
                                        </span>
                                    )}
                                </div>
                                
                                {book.status === 'Available' ? (
                                    <button className="mt-3 w-full flex justify-center items-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none">
                                        <FiBookOpen className="mr-2" /> Borrow
                                    </button>
                                ) : (
                                    <button className="mt-3 w-full flex justify-center items-center py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none">
                                        Return
                                    </button>
                                )}
                            </div>
                        </div>
                    ))
                ) : (
                    <div className="col-span-full text-center py-10">
                        <FiBookOpen className="mx-auto h-12 w-12 text-gray-400" />
                        <h3 className="mt-2 text-sm font-medium text-gray-900">No books found</h3>
                        <p className="mt-1 text-sm text-gray-500">
                            Try adjusting your search or browse all available books.
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
} 