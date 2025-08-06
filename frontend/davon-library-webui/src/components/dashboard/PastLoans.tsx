'use client';

import React, { useState, useEffect } from 'react';
import { FiBook, FiCalendar, FiCheck } from 'react-icons/fi';
import { authService } from '@/lib/services/auth-service';

interface Loan {
  id: number;
  book: {
    title: string;
    author: {
      firstName: string;
      lastName: string;
    };
  };
  borrowDate: string;
  returnDate: string;
  dueDate: string;
  status: string;
}

export default function PastLoans() {
  const [loans, setLoans] = useState<Loan[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPastLoans = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        if (!currentUser?.id) return;

        const response = await fetch(`http://localhost:8081/api/loans/user/${currentUser.id}`);
        if (response.ok) {
          const allLoans = await response.json();
          // Filter for returned loans (past loans)
          const pastLoans = allLoans
            .filter((loan: any) => loan.returnDate)
            .sort((a: any, b: any) => new Date(b.returnDate).getTime() - new Date(a.returnDate).getTime())
            .slice(0, 5); // Show last 5 returned books
          setLoans(pastLoans);
        }
      } catch (error) {
        console.error('Failed to fetch past loans:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchPastLoans();
  }, []);

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  };

  const isOverdue = (dueDate: string, returnDate: string) => {
    return new Date(returnDate) > new Date(dueDate);
  };

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-sm border">
        <div className="p-6 border-b border-gray-200">
          <h3 className="text-lg font-semibold text-gray-900">Past Loans</h3>
        </div>
        <div className="p-6">
          <div className="space-y-3">
            {[...Array(3)].map((_, i) => (
              <div key={i} className="animate-pulse">
                <div className="flex items-center space-x-3">
                  <div className="w-8 h-8 bg-gray-200 rounded-full"></div>
                  <div className="flex-1">
                    <div className="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
                    <div className="h-3 bg-gray-200 rounded w-1/2"></div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border">
      <div className="p-6 border-b border-gray-200">
        <h3 className="text-lg font-semibold text-gray-900">Past Loans</h3>
      </div>
      <div className="p-6">
        {loans.length === 0 ? (
          <div className="text-center py-8">
            <FiBook className="w-12 h-12 text-gray-400 mx-auto mb-3" />
            <p className="text-gray-500">No past loans</p>
            <p className="text-sm text-gray-400 mt-1">
              Your reading history will appear here
            </p>
          </div>
        ) : (
          <div className="space-y-4">
            {loans.map((loan) => (
              <div key={loan.id} className="flex items-start space-x-3">
                <div className="flex-shrink-0 mt-1">
                  <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                    isOverdue(loan.dueDate, loan.returnDate) 
                      ? 'bg-red-100 text-red-600' 
                      : 'bg-green-100 text-green-600'
                  }`}>
                    <FiCheck className="w-4 h-4" />
                  </div>
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900 truncate">
                    {loan.book.title}
                  </p>
                  <p className="text-sm text-gray-600">
                    by {loan.book.author.firstName} {loan.book.author.lastName}
                  </p>
                  <div className="flex items-center text-xs text-gray-500 mt-1 space-x-4">
                    <span className="flex items-center">
                      <FiCalendar className="w-3 h-3 mr-1" />
                      Returned {formatDate(loan.returnDate)}
                    </span>
                    {isOverdue(loan.dueDate, loan.returnDate) && (
                      <span className="text-red-500 font-medium">
                        Overdue
                      </span>
                    )}
                  </div>
                </div>
              </div>
            ))}
            
            {loans.length > 0 && (
              <div className="mt-4 pt-4 border-t border-gray-200">
                <button className="text-sm text-primary-600 hover:text-primary-800 font-medium">
                  View all past loans
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}