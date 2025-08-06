'use client';

import React, { useState, useEffect } from 'react';
import { FiClock, FiAlertTriangle, FiBook } from 'react-icons/fi';
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
  dueDate: string;
  status: string;
}

export default function IncomingDeadlines() {
  const [dueSoonLoans, setDueSoonLoans] = useState<Loan[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDueSoonLoans = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        if (!currentUser?.id) return;

        const response = await fetch(`http://localhost:8081/api/loans/user/${currentUser.id}`);
        if (response.ok) {
          const allLoans = await response.json();
          
          // Filter for books due within 5 days
          const today = new Date();
          const fiveDaysFromNow = new Date(today.getTime() + (5 * 24 * 60 * 60 * 1000));
          
          const dueSoon = allLoans
            .filter((loan: any) => {
              if (loan.status !== 'BORROWED' || !loan.dueDate) return false;
              const dueDate = new Date(loan.dueDate);
              return dueDate >= today && dueDate <= fiveDaysFromNow;
            })
            .sort((a: any, b: any) => new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime());
          
          setDueSoonLoans(dueSoon);
        }
      } catch (error) {
        console.error('Failed to fetch due soon loans:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDueSoonLoans();
  }, []);

  const getDaysUntilDue = (dueDate: string) => {
    const today = new Date();
    const due = new Date(dueDate);
    const diffTime = due.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  };

  const getUrgencyColor = (daysUntilDue: number) => {
    if (daysUntilDue <= 1) return 'bg-red-100 text-red-600';
    if (daysUntilDue <= 2) return 'bg-orange-100 text-orange-600';
    return 'bg-yellow-100 text-yellow-600';
  };

  const getUrgencyIcon = (daysUntilDue: number) => {
    if (daysUntilDue <= 1) return <FiAlertTriangle className="w-4 h-4" />;
    return <FiClock className="w-4 h-4" />;
  };

  const formatDueDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric'
    });
  };

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-sm border">
        <div className="p-6 border-b border-gray-200">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-semibold text-gray-900">Due Soon</h3>
            <div className="h-6 w-16 bg-gray-200 rounded-full animate-pulse"></div>
          </div>
        </div>
        <div className="p-6">
          <div className="space-y-3">
            {[...Array(2)].map((_, i) => (
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
        <div className="flex items-center justify-between">
          <h3 className="text-lg font-semibold text-gray-900">Due Soon</h3>
          <span className="bg-red-100 text-red-800 text-xs px-2 py-1 rounded-full">
            {dueSoonLoans.length} {dueSoonLoans.length === 1 ? 'book' : 'books'}
          </span>
        </div>
      </div>
      <div className="p-6">
        {dueSoonLoans.length === 0 ? (
          <div className="text-center py-8">
            <FiBook className="w-12 h-12 text-gray-400 mx-auto mb-3" />
            <p className="text-gray-500">All caught up!</p>
            <p className="text-sm text-gray-400 mt-1">
              No books due in the next 5 days
            </p>
          </div>
        ) : (
          <div className="space-y-4">
            {dueSoonLoans.map((loan) => {
              const daysUntilDue = getDaysUntilDue(loan.dueDate);
              return (
                <div key={loan.id} className="flex items-start space-x-3">
                  <div className="flex-shrink-0 mt-1">
                    <div className={`w-8 h-8 rounded-full flex items-center justify-center ${getUrgencyColor(daysUntilDue)}`}>
                      {getUrgencyIcon(daysUntilDue)}
                    </div>
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900 truncate">
                      {loan.book.title}
                    </p>
                    <p className="text-sm text-gray-600">
                      by {loan.book.author.firstName} {loan.book.author.lastName}
                    </p>
                    <div className="flex items-center justify-between mt-2">
                      <span className="text-xs text-gray-500">
                        Due {formatDueDate(loan.dueDate)}
                      </span>
                      <span className={`text-xs px-2 py-1 rounded-full font-medium ${
                        daysUntilDue === 0 
                          ? 'bg-red-100 text-red-700' 
                          : daysUntilDue === 1 
                          ? 'bg-orange-100 text-orange-700'
                          : 'bg-yellow-100 text-yellow-700'
                      }`}>
                        {daysUntilDue === 0 
                          ? 'Due today'
                          : daysUntilDue === 1 
                          ? 'Due tomorrow'
                          : `${daysUntilDue} days left`
                        }
                      </span>
                    </div>
                  </div>
                </div>
              );
            })}
            
            {dueSoonLoans.length > 0 && (
              <div className="mt-4 pt-4 border-t border-gray-200">
                <button className="w-full text-sm text-primary-600 hover:text-primary-800 font-medium py-2 px-4 bg-primary-50 hover:bg-primary-100 rounded-lg transition-colors">
                  Extend due dates
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}