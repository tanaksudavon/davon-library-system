'use client';

import { useState, useEffect } from 'react';
import { userService } from '@/lib/services/user-service';
import { bookService } from '@/lib/services/book-service';

const StatCard = ({ title, value, description, icon }: { title: string; value: string | number; description?: string; icon: React.ReactNode }) => (
  <div className="bg-white rounded-lg shadow p-6">
    <div className="flex items-center justify-between mb-2">
      <div>
        <p className="text-sm font-medium text-gray-600">{title}</p>
        <p className="text-2xl font-semibold text-gray-900">{value}</p>
      </div>
      <div className="text-primary-500">{icon}</div>
    </div>
    {description && <p className="text-sm text-gray-500">{description}</p>}
  </div>
);

export default function DashboardPage() {
  const [stats, setStats] = useState({
    totalUsers: 0,
    activeUsers: 0,
    totalBooks: 0,
    availableBooks: 0,
    borrowedBooks: 0,
    recentActivities: [] as { id: string; action: string; details: string; time: string }[]
  });

  useEffect(() => {
    // Get user and book statistics
    const users = userService.getAllUsers();
    const books = bookService.getAllBooks();
    
    const availableBooks = books.filter(book => book.status === 'available');
    const borrowedBooks = books.filter(book => book.status === 'borrowed');
    
    // Create recent activities (can be integrated with real data)
    const activities = [
      { id: '1', action: 'Book Borrowed', details: 'Harry Potter - J.K. Rowling', time: '10 minutes ago' },
      { id: '2', action: 'New Member', details: 'John Smith registered', time: '30 minutes ago' },
      { id: '3', action: 'Book Returned', details: 'Crime and Punishment - Dostoyevsky', time: '1 hour ago' },
      { id: '4', action: 'New Book Added', details: '1984 - George Orwell', time: '2 hours ago' }
    ];
    
    setStats({
      totalUsers: users.length,
      activeUsers: users.filter(user => user.role === 'user').length,
      totalBooks: books.length,
      availableBooks: availableBooks.length,
      borrowedBooks: borrowedBooks.length,
      recentActivities: activities
    });
  }, []);

  return (
    <div className="p-6">
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Library Management Dashboard</h1>
        <p className="mt-1 text-sm text-gray-600">Current statistics and activities</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        <StatCard
          title="Total Members"
          value={stats.totalUsers}
          description="All registered users"
          icon={
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
            </svg>
          }
        />
        <StatCard
          title="Regular Members"
          value={stats.activeUsers}
          description="Users with regular member role"
          icon={
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5.121 17.804A13.937 13.937 0 0112 16c2.5 0 4.847.655 6.879 1.804M15 10a3 3 0 11-6 0 3 3 0 016 0zm6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          }
        />
        <StatCard
          title="Total Books"
          value={stats.totalBooks}
          description="Total number of books in library"
          icon={
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
          }
        />
        <StatCard
          title="Available Books"
          value={stats.availableBooks}
          description="Books available for borrowing"
          icon={
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          }
        />
        <StatCard
          title="Borrowed Books"
          value={stats.borrowedBooks}
          description="Currently borrowed books"
          icon={
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          }
        />
      </div>

      {/* Recent Activities */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Recent Activities</h2>
        <div className="space-y-4">
          {stats.recentActivities.map((activity) => (
            <div key={activity.id} className="flex items-center space-x-4">
              <div className="flex-shrink-0">
                <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center">
                  <svg className="w-4 h-4 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
              </div>
              <div>
                <p className="text-sm font-medium text-gray-900">{activity.action}</p>
                <p className="text-xs text-gray-500">{activity.details}</p>
                <p className="text-xs text-gray-400">{activity.time}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
} 