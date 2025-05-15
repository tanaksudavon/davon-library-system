'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/lib/store/auth-store';
import { FiClock, FiCheck, FiX } from 'react-icons/fi';

export default function UserHistoryPage() {
    const router = useRouter();
    const { user } = useAuthStore();
    
    // Sample loan history data (in a real app, this would come from an API)
    const [loanHistory, setLoanHistory] = useState([
        { 
            id: 1, 
            bookTitle: 'The Hobbit', 
            bookAuthor: 'J.R.R. Tolkien',
            borrowDate: '2023-01-15', 
            returnDate: '2023-02-01', 
            status: 'Returned' 
        },
        { 
            id: 2, 
            bookTitle: 'Harry Potter and the Philosopher\'s Stone', 
            bookAuthor: 'J.K. Rowling',
            borrowDate: '2023-02-10', 
            returnDate: '2023-03-01', 
            status: 'Returned' 
        },
        { 
            id: 3, 
            bookTitle: 'The Catcher in the Rye', 
            bookAuthor: 'J.D. Salinger',
            borrowDate: '2023-04-05', 
            dueDate: '2023-05-05',
            status: 'Overdue' 
        },
        { 
            id: 4, 
            bookTitle: '1984', 
            bookAuthor: 'George Orwell',
            borrowDate: '2023-04-20', 
            dueDate: '2023-05-20',
            status: 'Borrowed' 
        },
    ]);

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
                    <p className="mt-2">Please log in to view your loan history</p>
                </div>
            </div>
        );
    }

    return (
        <div className="container mx-auto">
            <h1 className="text-2xl font-bold text-slate-800 mb-6">Loan History</h1>
            
            <div className="bg-white shadow-sm rounded-lg overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-50">
                            <tr>
                                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Book
                                </th>
                                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Borrow Date
                                </th>
                                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Return/Due Date
                                </th>
                                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Status
                                </th>
                            </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                            {loanHistory.map((loan) => (
                                <tr key={loan.id}>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <div className="text-sm font-medium text-gray-900">{loan.bookTitle}</div>
                                        <div className="text-sm text-gray-500">{loan.bookAuthor}</div>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <div className="text-sm text-gray-900">
                                            {new Date(loan.borrowDate).toLocaleDateString()}
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <div className="text-sm text-gray-900">
                                            {loan.returnDate
                                                ? new Date(loan.returnDate).toLocaleDateString()
                                                : loan.dueDate ? new Date(loan.dueDate).toLocaleDateString() : 'N/A'}
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                                            loan.status === 'Returned'
                                                ? 'bg-green-100 text-green-800'
                                                : loan.status === 'Overdue'
                                                ? 'bg-red-100 text-red-800'
                                                : 'bg-blue-100 text-blue-800'
                                        }`}>
                                            {loan.status === 'Returned' && <FiCheck className="mr-1" />}
                                            {loan.status === 'Overdue' && <FiX className="mr-1" />}
                                            {loan.status === 'Borrowed' && <FiClock className="mr-1" />}
                                            {loan.status}
                                        </span>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
            
            <div className="mt-6">
                <div className="bg-white shadow-sm rounded-lg p-6">
                    <h3 className="text-lg font-medium text-gray-900 mb-4">Library Policies</h3>
                    <ul className="space-y-2 text-sm text-gray-600">
                        <li className="flex items-start">
                            <span className="flex-shrink-0 h-5 w-5 text-green-500 mr-2">•</span>
                            <span>Standard loan period is 30 days.</span>
                        </li>
                        <li className="flex items-start">
                            <span className="flex-shrink-0 h-5 w-5 text-green-500 mr-2">•</span>
                            <span>You may renew books up to 2 times if no one else has requested them.</span>
                        </li>
                        <li className="flex items-start">
                            <span className="flex-shrink-0 h-5 w-5 text-green-500 mr-2">•</span>
                            <span>Late fees are $0.25 per day for overdue items.</span>
                        </li>
                        <li className="flex items-start">
                            <span className="flex-shrink-0 h-5 w-5 text-green-500 mr-2">•</span>
                            <span>Please return books in good condition to avoid damage fees.</span>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    );
} 