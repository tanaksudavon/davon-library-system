'use client';

import React, { useState, useEffect } from 'react';

interface Activity {
  id: string;
  type: 'borrowed' | 'returned' | 'available' | 'reserved';
  userName: string;
  bookTitle: string;
  timestamp: string;
}

export default function RecentActivities() {
  const [activities, setActivities] = useState<Activity[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchActivities = async () => {
      try {
        // Fetch recent loans from the API
        const response = await fetch('http://localhost:8081/api/loans');
        if (response.ok) {
          const loans = await response.json();
          
          // Convert recent loans to activities (last 10 loans)
          const recentActivities: Activity[] = loans
            .slice(-10) // Get last 10 loans
            .reverse() // Show most recent first
            .map((loan: any, index: number) => ({
              id: loan.id.toString(),
              type: loan.returnDate ? 'returned' : 'borrowed' as 'borrowed' | 'returned',
              userName: `${loan.user?.firstName || 'Unknown'} ${loan.user?.lastName || 'User'}`,
              bookTitle: loan.book?.title || 'Unknown Book',
              timestamp: loan.returnDate || loan.borrowDate || loan.createdAt
            }));
          
          setActivities(recentActivities);
        } else {
          console.error('Failed to fetch loans');
          // Fall back to empty array
          setActivities([]);
        }
      } catch (error) {
        console.error('Error fetching activities:', error);
        setActivities([]);
      } finally {
        setLoading(false);
      }
    };

    fetchActivities();
  }, []);

  const formatTime = (timestamp: string) => {
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    
    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    return date.toLocaleDateString();
  };

  const getActivityIcon = (type: string) => {
    switch (type) {
      case 'borrowed':
        return '📚';
      case 'returned':
        return '✅';
      case 'available':
        return '🟢';
      case 'reserved':
        return '🔖';
      default:
        return '📋';
    }
  };

  const getActivityText = (activity: Activity) => {
    switch (activity.type) {
      case 'borrowed':
        return `${activity.userName} borrowed "${activity.bookTitle}"`;
      case 'returned':
        return `${activity.userName} returned "${activity.bookTitle}"`;
      case 'available':
        return `"${activity.bookTitle}" is now available`;
      case 'reserved':
        return `${activity.userName} reserved "${activity.bookTitle}"`;
      default:
        return 'Unknown activity';
    }
  };

  const getActivityColor = (type: string) => {
    switch (type) {
      case 'borrowed':
        return 'text-blue-600';
      case 'returned':
        return 'text-green-600';
      case 'available':
        return 'text-emerald-600';
      case 'reserved':
        return 'text-orange-600';
      default:
        return 'text-gray-600';
    }
  };

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Recent Activities</h3>
        <div className="space-y-3">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="flex items-center space-x-3 animate-pulse">
              <div className="w-8 h-8 bg-gray-200 rounded-full"></div>
              <div className="flex-1">
                <div className="h-4 bg-gray-200 rounded w-3/4 mb-1"></div>
                <div className="h-3 bg-gray-200 rounded w-1/2"></div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">Recent Activities</h3>
      <div className="space-y-3">
        {activities.length === 0 ? (
          <p className="text-gray-500 text-center py-4">No recent activities</p>
        ) : (
          activities.map((activity) => (
            <div key={activity.id} className="flex items-start space-x-3 p-3 hover:bg-gray-50 rounded-lg">
              <div className="flex-shrink-0">
                <span className="text-xl">{getActivityIcon(activity.type)}</span>
              </div>
              <div className="flex-1 min-w-0">
                <p className={`text-sm font-medium ${getActivityColor(activity.type)}`}>
                  {getActivityText(activity)}
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  {formatTime(activity.timestamp)}
                </p>
              </div>
            </div>
          ))
        )}
      </div>
      {activities.length > 0 && (
        <div className="mt-4 pt-4 border-t border-gray-200">
          <button className="text-sm text-blue-600 hover:text-blue-800 font-medium">
            View all activities
          </button>
        </div>
      )}
    </div>
  );
} 