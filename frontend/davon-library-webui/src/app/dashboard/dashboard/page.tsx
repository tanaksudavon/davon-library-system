'use client';

import { useState, useEffect } from 'react';
import { userService } from '@/lib/api/services/user.service';
import { bookService } from '@/lib/api/services/book.service';
import { authService } from '@/lib/services/auth-service';
import { User, Book, BookStatus, UserRole } from '@/lib/api/types';
import RecentActivities from '@/components/dashboard/RecentActivities';
import ReservationQueue from '@/components/reservations/ReservationQueue';
import PastLoans from '@/components/dashboard/PastLoans';
import IncomingDeadlines from '@/components/dashboard/IncomingDeadlines';
import FavoriteBooks from '@/components/dashboard/FavoriteBooks';
import { 
  FiUsers, FiBook, FiBookOpen, FiClock, FiTrendingUp, 
  FiActivity, FiUserCheck, FiCalendar, FiRefreshCw 
} from 'react-icons/fi';

interface DashboardStats {
  totalUsers: number;
  regularUsers: number;
  librarians: number;
  totalBooks: number;
  availableBooks: number;
  borrowedBooks: number;
  reservedBooks: number;
  userActiveLoans?: number;
  userReservations?: number;
  dueSoon?: number;
  recentActivities: { 
    id: string; 
    type: 'book' | 'user'; 
    action: string; 
    details: string; 
    time: string;
    icon: React.ReactNode;
  }[];
}

const StatCard = ({ 
  title, 
  value, 
  description, 
  icon, 
  trend,
  color = 'bg-white'
}: { 
  title: string; 
  value: string | number; 
  description?: string; 
  icon: React.ReactNode;
  trend?: { value: string; isPositive: boolean };
  color?: string;
}) => (
  <div className={`${color} rounded-lg shadow-sm border p-6 hover:shadow-md transition-shadow`}>
    <div className="flex items-start justify-between">
      <div className="flex-1">
        <p className="text-sm font-medium text-gray-600 mb-1">{title}</p>
        <p className="text-3xl font-bold text-gray-900 mb-2">{value}</p>
        {description && <p className="text-sm text-gray-500">{description}</p>}
        {trend && (
          <div className={`flex items-center mt-2 text-sm ${trend.isPositive ? 'text-green-600' : 'text-red-600'}`}>
            <FiTrendingUp className="w-4 h-4 mr-1" />
            {trend.value}
          </div>
        )}
      </div>
      <div className="text-primary-500 opacity-80">{icon}</div>
    </div>
  </div>
);

const ActivityItem = ({ activity }: { activity: DashboardStats['recentActivities'][0] }) => (
  <div className="flex items-start space-x-3 p-4 hover:bg-gray-50 rounded-lg transition-colors">
    <div className="flex-shrink-0 mt-1">
      <div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
        {activity.icon}
      </div>
    </div>
    <div className="flex-1 min-w-0">
      <p className="text-sm font-medium text-gray-900">{activity.action}</p>
      <p className="text-sm text-gray-600 truncate">{activity.details}</p>
      <p className="text-xs text-gray-400 flex items-center mt-1">
        <FiClock className="w-3 h-3 mr-1" />
        {activity.time}
      </p>
    </div>
  </div>
);

export default function DashboardPage() {
  const [stats, setStats] = useState<DashboardStats>({
    totalUsers: 0,
    regularUsers: 0,
    librarians: 0,
    totalBooks: 0,
    availableBooks: 0,
    borrowedBooks: 0,
    reservedBooks: 0,
    recentActivities: []
  });
  
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [lastRefresh, setLastRefresh] = useState<Date>(new Date());

  const currentUser = authService.getCurrentUser();
  const isLibrarian = currentUser?.role === UserRole.LIBRARIAN;

  const loadDashboardData = async () => {
    setLoading(true);
    setError(null);
    try {
      if (isLibrarian) {
        // Load admin data
        const [users, books] = await Promise.all([
          userService.getAllUsers(),
          bookService.getAllBooks()
        ]);

        // Calculate statistics
        const regularUsers = users.filter(user => user.role === UserRole.MEMBER).length;
        const librarians = users.filter(user => user.role === UserRole.LIBRARIAN).length;
        const availableBooks = books.filter(book => book.status === BookStatus.AVAILABLE).length;
        const borrowedBooks = books.filter(book => book.status === BookStatus.BORROWED).length;
        const reservedBooks = books.filter(book => book.status === BookStatus.RESERVED).length;

        // Generate recent activities
        const activities = generateRecentActivities(books, users);

        setStats({
          totalUsers: users.length,
          regularUsers,
          librarians,
          totalBooks: books.length,
          availableBooks,
          borrowedBooks,
          reservedBooks,
          recentActivities: activities
        });
      } else {
        // Load user-specific data
        const userId = currentUser?.id;
        if (!userId) return;

        const [books, userLoansResponse, userReservationsResponse] = await Promise.all([
          bookService.getAllBooks(),
          fetch(`http://localhost:8081/api/loans/user/${userId}`).then(res => res.ok ? res.json() : []),
          fetch(`http://localhost:8081/api/reservations/user/${userId}`).then(res => res.ok ? res.json() : [])
        ]);

        // Calculate user-specific stats
        const userLoans = userLoansResponse || [];
        const userReservations = userReservationsResponse || [];
        const userActiveLoans = userLoans.filter((loan: any) => loan.status === 'BORROWED').length;
        const activeReservations = userReservations.filter((res: any) => res.status === 'ACTIVE').length;
        
        // Calculate books due within 5 days
        const today = new Date();
        const fiveDaysFromNow = new Date(today.getTime() + (5 * 24 * 60 * 60 * 1000));
        const dueSoon = userLoans.filter((loan: any) => {
          if (loan.status !== 'BORROWED' || !loan.dueDate) return false;
          const dueDate = new Date(loan.dueDate);
          return dueDate >= today && dueDate <= fiveDaysFromNow;
        }).length;

        // Generate user-focused activities
        const activities = generateUserActivities(userLoans);

        setStats({
          totalUsers: 0,
          regularUsers: 0,
          librarians: 0,
          totalBooks: books.length,
          availableBooks: 0,
          borrowedBooks: 0,
          reservedBooks: 0,
          userActiveLoans,
          userReservations: activeReservations,
          dueSoon,
          recentActivities: activities
        });
      }

      setLastRefresh(new Date());
    } catch (error) {
      console.error('Failed to load dashboard data:', error);
      setError('Failed to load dashboard data. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const generateRecentActivities = (books: Book[], users: User[]): DashboardStats['recentActivities'] => {
    const activities: DashboardStats['recentActivities'] = [];

    // Add recent books
    books.slice(0, 3).forEach((book, index) => {
      activities.push({
        id: `book-${book.id}`,
        type: 'book',
        action: book.status === BookStatus.BORROWED ? 'Book Borrowed' : 'Book Added',
        details: `"${book.title}" by ${book.author.firstName} ${book.author.lastName}`,
        time: `${index + 1} hour${index > 0 ? 's' : ''} ago`,
        icon: <FiBook className="w-4 h-4 text-primary-600" />
      });
    });

    // Add recent users
    users.slice(0, 3).forEach((user, index) => {
      if (user.role === UserRole.MEMBER) {
        activities.push({
          id: `user-${user.id}`,
          type: 'user',
          action: 'New Member Registered',
          details: `${user.firstName} ${user.lastName} joined the library`,
          time: `${index + 2} hours ago`,
          icon: <FiUserCheck className="w-4 h-4 text-green-600" />
        });
      }
    });

    return activities.slice(0, 6);
  };

  const generateUserActivities = (userLoans: any[]): DashboardStats['recentActivities'] => {
    const activities: DashboardStats['recentActivities'] = [];

    // Convert recent loans to activities (last 6 loans)
    userLoans
      .slice(-6) // Get last 6 loans
      .reverse() // Show most recent first
      .forEach((loan, index) => {
        activities.push({
          id: `loan-${loan.id}`,
          type: 'book',
          action: loan.returnDate ? 'Book Returned' : 'Book Borrowed',
          details: `"${loan.book?.title || 'Unknown Book'}" by ${loan.book?.author?.firstName || ''} ${loan.book?.author?.lastName || ''}`.trim(),
          time: `${index + 1} hour${index > 0 ? 's' : ''} ago`,
          icon: loan.returnDate ? 
            <FiBook className="w-4 h-4 text-green-600" /> : 
            <FiBookOpen className="w-4 h-4 text-primary-600" />
        });
      });

    return activities;
  };

  const handleRefresh = () => {
    loadDashboardData();
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  // Auto-refresh every 5 minutes
  /*useEffect(() => {
    const interval = setInterval(() => {
      loadDashboardData();
    }, 5 * 60 * 1000);

    return () => clearInterval(interval);
  }, []);*/

  if (loading && stats.totalUsers === 0) {
    return (
      <div className="flex items-center justify-center min-h-96">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-500 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading dashboard...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <div className="text-red-600 mb-4">
          <FiActivity className="w-12 h-12 mx-auto mb-2" />
          <h3 className="text-lg font-medium">Error Loading Dashboard</h3>
          <p className="text-sm">{error}</p>
        </div>
        <button
          onClick={handleRefresh}
          className="inline-flex items-center px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
        >
          <FiRefreshCw className="w-4 h-4 mr-2" />
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">
            Welcome back, {currentUser?.firstName}!
          </h1>
          <p className="text-gray-600">
            Here's what's happening in your library today.
          </p>
        </div>
        <div className="flex items-center space-x-3">
          <span className="text-sm text-gray-500">
            Last updated: {lastRefresh.toLocaleTimeString()}
          </span>
          <button
            onClick={handleRefresh}
            disabled={loading}
            className="inline-flex items-center px-3 py-2 text-sm bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50"
          >
            <FiRefreshCw className={`w-4 h-4 mr-2 ${loading ? 'animate-spin' : ''}`} />
            Refresh
          </button>
        </div>
      </div>

      {/* User vs Admin Statistics */}
      {isLibrarian ? (
        <>
          {/* Main Statistics Grid for Librarians */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            <StatCard
              title="Total Books"
              value={stats.totalBooks}
              description={`${stats.availableBooks} available`}
              icon={<FiBook className="w-6 h-6" />}
              trend={{ value: "+5 this week", isPositive: true }}
            />
            <StatCard
              title="Borrowed Books"
              value={stats.borrowedBooks}
              description="Currently on loan"
              icon={<FiBookOpen className="w-6 h-6" />}
              trend={{ value: "+12 this week", isPositive: true }}
            />
            <StatCard
              title="Library Members"
              value={stats.regularUsers}
              description={`${stats.totalUsers} total users`}
              icon={<FiUsers className="w-6 h-6" />}
              trend={{ value: "+3 this week", isPositive: true }}
            />
            <StatCard
              title="Reservations"
              value={stats.reservedBooks}
              description="Books reserved"
              icon={<FiCalendar className="w-6 h-6" />}
              trend={{ value: "+8 this week", isPositive: true }}
            />
          </div>

          {/* Librarian-only additional stats */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
            <StatCard
              title="Librarians"
              value={stats.librarians}
              description="Active staff members"
              icon={<FiUserCheck className="w-6 h-6" />}
              color="bg-purple-50"
            />
            <StatCard
              title="Available Books"
              value={stats.availableBooks}
              description="Ready to borrow"
              icon={<FiBook className="w-6 h-6" />}
              color="bg-green-50"
            />
            <StatCard
              title="System Activity"
              value={stats.recentActivities.length}
              description="Recent activities"
              icon={<FiActivity className="w-6 h-6" />}
              color="bg-blue-50"
            />
          </div>
        </>
      ) : (
        /* User-focused stats - Quick overview cards */
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
          <StatCard
            title="My Active Loans"
            value={stats.userActiveLoans || 0}
            description="Books currently borrowed"
            icon={<FiBookOpen className="w-6 h-6" />}
            color="bg-blue-50"
          />
          <StatCard
            title="My Reservations"
            value={stats.userReservations || 0}
            description="Books in queue"
            icon={<FiCalendar className="w-6 h-6" />}
            color="bg-orange-50"
          />
          <StatCard
            title="Due Soon"
            value={stats.dueSoon || 0}
            description="Books due within 5 days"
            icon={<FiClock className="w-6 h-6" />}
            color="bg-red-50"
          />
        </div>
      )}

      {/* Main Dashboard Content */}
      {isLibrarian ? (
        /* Librarian Dashboard Layout */
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Recent Activities */}
          <RecentActivities />

          {/* Library Overview for Librarians */}
          <div className="bg-white rounded-lg shadow-sm border">
            <div className="p-6 border-b border-gray-200">
              <h3 className="text-lg font-semibold text-gray-900">Library Overview</h3>
            </div>
            <div className="p-6 space-y-4">
              <div className="flex justify-between items-center py-3 border-b border-gray-100">
                <span className="text-gray-600">Books in circulation</span>
                <span className="font-semibold text-primary-600">{stats.borrowedBooks}</span>
              </div>
              <div className="flex justify-between items-center py-3 border-b border-gray-100">
                <span className="text-gray-600">Available for borrowing</span>
                <span className="font-semibold text-green-600">{stats.availableBooks}</span>
              </div>
              <div className="flex justify-between items-center py-3 border-b border-gray-100">
                <span className="text-gray-600">Books reserved</span>
                <span className="font-semibold text-orange-600">{stats.reservedBooks}</span>
              </div>
              <div className="flex justify-between items-center py-3 border-b border-gray-100">
                <span className="text-gray-600">Active members</span>
                <span className="font-semibold text-blue-600">{stats.regularUsers}</span>
              </div>
              <div className="flex justify-between items-center py-3">
                <span className="text-gray-600">Staff members</span>
                <span className="font-semibold text-purple-600">{stats.librarians}</span>
              </div>
            </div>
          </div>
        </div>
      ) : (
        /* User Dashboard Layout */
        <div className="space-y-6">
          {/* Top Row - My Reservations and Recent Activities */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <ReservationQueue userId={currentUser?.id || 0} />
            
            {/* Compact Recent Activities */}
            <div className="bg-white rounded-lg shadow-sm border">
              <div className="p-6 border-b border-gray-200">
                <h3 className="text-lg font-semibold text-gray-900">Recent Activities</h3>
              </div>
              <div className="p-6">
                {stats.recentActivities.length === 0 ? (
                  <div className="text-center py-8">
                    <FiActivity className="w-12 h-12 text-gray-400 mx-auto mb-3" />
                    <p className="text-gray-500">No recent activity</p>
                  </div>
                ) : (
                  <div className="space-y-3">
                    {stats.recentActivities.slice(0, 3).map((activity) => (
                      <ActivityItem key={activity.id} activity={activity} />
                    ))}
                  </div>
                )}
                {stats.recentActivities.length > 3 && (
                  <div className="mt-4 pt-4 border-t border-gray-200">
                    <button className="text-sm text-primary-600 hover:text-primary-800 font-medium">
                      View all activities
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Bottom Row - Past Loans, Due Soon, and Favorites */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Past Loans */}
            <PastLoans />

            {/* Incoming Deadlines */}
            <IncomingDeadlines />

            {/* Favorite Books */}
            <FavoriteBooks />
          </div>
        </div>
      )}
    </div>
  );
} 