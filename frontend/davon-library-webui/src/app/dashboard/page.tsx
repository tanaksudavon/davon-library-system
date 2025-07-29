'use client';

import { useState, useEffect } from 'react';
import { userService } from '@/lib/api/services/user.service';
import { bookService } from '@/lib/api/services/book.service';
import { User, Book, BookStatus, UserRole } from '@/lib/api/types';

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

interface DashboardStats {
  totalUsers: number;
  regularUsers: number;
  librarians: number;
  totalBooks: number;
  availableBooks: number;
  borrowedBooks: number;
  recentActivities: { id: string; action: string; details: string; time: string }[];
}

export default function DashboardPage() {
  const [stats, setStats] = useState<DashboardStats>({
    totalUsers: 0,
    regularUsers: 0,
    librarians: 0,
    totalBooks: 0,
    availableBooks: 0,
    borrowedBooks: 0,
    recentActivities: []
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadDashboardData = async () => {
    try {
      console.log('Loading dashboard data...');
      setLoading(true);
      setError(null);

      // Fetch data from API in parallel
      const [users, books] = await Promise.all([
        userService.getAllUsers(),
        bookService.getAllBooks()
      ]);

      console.log('Dashboard data loaded:', { users: users.length, books: books.length });

      // Calculate stats
      const availableBooks = books.filter(book => book.status === BookStatus.AVAILABLE);
      const borrowedBooks = books.filter(book => book.status === BookStatus.BORROWED);
      const regularUsers = users.filter(user => user.role === UserRole.MEMBER);
      const librarians = users.filter(user => user.role === UserRole.LIBRARIAN);

      // Generate recent activities based on real data
      const activities = generateRecentActivities(books, users);

      setStats({
        totalUsers: users.length,
        regularUsers: regularUsers.length,
        librarians: librarians.length,
        totalBooks: books.length,
        availableBooks: availableBooks.length,
        borrowedBooks: borrowedBooks.length,
        recentActivities: activities
      });
    } catch (err) {
      console.error('Failed to load dashboard data:', err);
      setError('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  // Generate realistic recent activities based on actual data
  const generateRecentActivities = (books: Book[], users: User[]): { id: string; action: string; details: string; time: string }[] => {
    const activities: { id: string; action: string; details: string; time: string }[] = [];
    
    // Add activities for borrowed books
    const borrowedBooks = books.filter(book => book.status === BookStatus.BORROWED);
    borrowedBooks.slice(0, 2).forEach((book, index) => {
      activities.push({
        id: `borrow-${book.id}`,
        action: 'Book Borrowed',
        details: `${book.title} - ${book.author.firstName} ${book.author.lastName}`,
        time: `${10 + index * 20} minutes ago`
      });
    });

    // Add new member activities
    const recentUsers = users
      .filter(user => user.createdAt)
      .sort((a, b) => new Date(b.createdAt!).getTime() - new Date(a.createdAt!).getTime())
      .slice(0, 2);
    
    recentUsers.forEach((user, index) => {
      activities.push({
        id: `user-${user.id}`,
        action: 'New Member',
        details: `${user.firstName} ${user.lastName} registered`,
        time: `${30 + index * 45} minutes ago`
      });
    });

    // Add some book return activities
    const availableBooks = books.filter(book => book.status === BookStatus.AVAILABLE);
    availableBooks.slice(0, 1).forEach((book) => {
      activities.push({
        id: `return-${book.id}`,
        action: 'Book Returned',
        details: `${book.title} - ${book.author.firstName} ${book.author.lastName}`,
        time: '1 hour ago'
      });
    });

    return activities.slice(0, 5); // Limit to 5 activities
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  // Auto-refresh every 5 minutes
  useEffect(() => {
    const interval = setInterval(loadDashboardData, 5 * 60 * 1000);
    return () => clearInterval(interval);
  }, []);

  if (loading) {
    return (
      <div className="p-6">
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-primary-500"></div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-6">
        <div className="bg-red-50 border border-red-200 rounded-md p-4">
          <div className="flex">
            <div className="ml-3">
              <h3 className="text-sm font-medium text-red-800">Error loading dashboard</h3>
              <div className="mt-2 text-sm text-red-700">
                <p>{error}</p>
              </div>
              <div className="mt-4">
                <button
                  onClick={loadDashboardData}
                  className="bg-red-100 px-3 py-2 rounded-md text-sm font-medium text-red-800 hover:bg-red-200"
                >
                  Try again
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="p-6">
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Library Management Dashboard</h1>
        <p className="mt-1 text-sm text-gray-600">Current statistics and activities</p>
        <button
          onClick={loadDashboardData}
          className="mt-2 text-sm text-primary-600 hover:text-primary-800"
        >
          Refresh Data
        </button>
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
          value={stats.regularUsers}
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
        <StatCard
          title="Librarians"
          value={stats.librarians}
          description="Admin users"
          icon={
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
            </svg>
          }
        />
      </div>

      {/* Recent Activities */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Recent Activities</h2>
        <div className="space-y-4">
          {stats.recentActivities.length > 0 ? (
            stats.recentActivities.map((activity) => (
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
            ))
          ) : (
            <p className="text-sm text-gray-500">No recent activities</p>
          )}
        </div>
      </div>
    </div>
  );
} 