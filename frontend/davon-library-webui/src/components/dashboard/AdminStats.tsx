'use client';

import { FiUsers, FiBook, FiBookOpen, FiTrendingUp } from 'react-icons/fi';

interface AdminStatsProps {
  totalUsers: number;
  activeUsers: number;
  totalBooks: number;
  availableBooks: number;
  borrowedBooks: number;
}

interface StatCardProps {
  title: string;
  value: number;
  icon: React.ReactNode;
  color: string;
  change?: {
    value: number;
    type: 'increase' | 'decrease';
  };
}

function StatCard({ title, value, icon, color, change }: StatCardProps) {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className="text-3xl font-bold text-gray-900 mt-2">{value.toLocaleString()}</p>
          {change && (
            <div className={`flex items-center mt-2 text-sm ${
              change.type === 'increase' ? 'text-green-600' : 'text-red-600'
            }`}>
              <FiTrendingUp className={`w-4 h-4 mr-1 ${
                change.type === 'decrease' ? 'rotate-180' : ''
              }`} />
              <span>{change.value}% from last month</span>
            </div>
          )}
        </div>
        <div className={`p-3 rounded-full ${color}`}>
          {icon}
        </div>
      </div>
    </div>
  );
}

export default function AdminStats({
  totalUsers,
  activeUsers,
  totalBooks,
  availableBooks,
  borrowedBooks
}: AdminStatsProps) {
  const borrowRate = totalBooks > 0 ? ((borrowedBooks / totalBooks) * 100).toFixed(1) : '0';
  const availabilityRate = totalBooks > 0 ? ((availableBooks / totalBooks) * 100).toFixed(1) : '0';

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <StatCard
        title="Total Users"
        value={totalUsers}
        icon={<FiUsers className="w-6 h-6 text-blue-600" />}
        color="bg-blue-100"
        change={{ value: 12, type: 'increase' }}
      />
      
      <StatCard
        title="Active Members"
        value={activeUsers}
        icon={<FiUsers className="w-6 h-6 text-green-600" />}
        color="bg-green-100"
        change={{ value: 8, type: 'increase' }}
      />
      
      <StatCard
        title="Total Books"
        value={totalBooks}
        icon={<FiBook className="w-6 h-6 text-purple-600" />}
        color="bg-purple-100"
        change={{ value: 5, type: 'increase' }}
      />
      
      <StatCard
        title="Books Borrowed"
        value={borrowedBooks}
        icon={<FiBookOpen className="w-6 h-6 text-orange-600" />}
        color="bg-orange-100"
        change={{ value: 15, type: 'increase' }}
      />
      
      {/* Additional Stats Row */}
      <div className="lg:col-span-4 grid grid-cols-1 md:grid-cols-2 gap-6 mt-4">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Book Statistics</h3>
          <div className="space-y-4">
            <div className="flex justify-between items-center">
              <span className="text-gray-600">Available Books</span>
              <div className="flex items-center">
                <span className="font-semibold text-green-600">{availableBooks}</span>
                <span className="text-sm text-gray-500 ml-2">({availabilityRate}%)</span>
              </div>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-gray-600">Borrowed Books</span>
              <div className="flex items-center">
                <span className="font-semibold text-orange-600">{borrowedBooks}</span>
                <span className="text-sm text-gray-500 ml-2">({borrowRate}%)</span>
              </div>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div
                className="bg-green-500 h-2 rounded-full"
                style={{ width: `${availabilityRate}%` }}
              ></div>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h3>
          <div className="space-y-3">
            <button className="w-full text-left px-4 py-2 bg-blue-50 text-blue-700 rounded-lg hover:bg-blue-100 transition-colors">
              Add New Book
            </button>
            <button className="w-full text-left px-4 py-2 bg-green-50 text-green-700 rounded-lg hover:bg-green-100 transition-colors">
              Register New User
            </button>
            <button className="w-full text-left px-4 py-2 bg-purple-50 text-purple-700 rounded-lg hover:bg-purple-100 transition-colors">
              View Reports
            </button>
            <button className="w-full text-left px-4 py-2 bg-orange-50 text-orange-700 rounded-lg hover:bg-orange-100 transition-colors">
              Manage Categories
            </button>
          </div>
        </div>
      </div>
    </div>
  );
} 