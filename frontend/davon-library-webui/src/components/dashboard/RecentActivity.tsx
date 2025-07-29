'use client';

import { FiBookOpen, FiUser, FiPlus, FiRotateCcw, FiClock } from 'react-icons/fi';

interface Activity {
  id: string;
  action: string;
  details: string;
  time: string;
  type: 'borrow' | 'return' | 'register' | 'add_book' | 'other';
}

interface RecentActivityProps {
  activities: Activity[];
  maxItems?: number;
}

function ActivityIcon({ type }: { type: Activity['type'] }) {
  const iconProps = "w-4 h-4";
  
  switch (type) {
    case 'borrow':
      return <FiBookOpen className={`${iconProps} text-blue-600`} />;
    case 'return':
      return <FiRotateCcw className={`${iconProps} text-green-600`} />;
    case 'register':
      return <FiUser className={`${iconProps} text-purple-600`} />;
    case 'add_book':
      return <FiPlus className={`${iconProps} text-orange-600`} />;
    default:
      return <FiClock className={`${iconProps} text-gray-600`} />;
  }
}

function getActivityColor(type: Activity['type']) {
  switch (type) {
    case 'borrow':
      return 'bg-blue-100';
    case 'return':
      return 'bg-green-100';
    case 'register':
      return 'bg-purple-100';
    case 'add_book':
      return 'bg-orange-100';
    default:
      return 'bg-gray-100';
  }
}

function formatTimeAgo(timeString: string): string {
  // Simple time formatting - in a real app, you'd use a library like date-fns
  const now = new Date();
  const time = new Date(timeString);
  const diffInMinutes = Math.floor((now.getTime() - time.getTime()) / (1000 * 60));
  
  if (diffInMinutes < 1) return 'Just now';
  if (diffInMinutes < 60) return `${diffInMinutes} minutes ago`;
  if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)} hours ago`;
  return `${Math.floor(diffInMinutes / 1440)} days ago`;
}

export default function RecentActivity({ 
  activities, 
  maxItems = 10 
}: RecentActivityProps) {
  const displayedActivities = activities.slice(0, maxItems);

  if (activities.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Recent Activity</h3>
        <div className="text-center py-8">
          <FiClock className="w-12 h-12 text-gray-400 mx-auto mb-3" />
          <p className="text-gray-500">No recent activity</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-gray-900">Recent Activity</h3>
        <span className="text-sm text-gray-500">
          {activities.length} {activities.length === 1 ? 'activity' : 'activities'}
        </span>
      </div>
      
      <div className="space-y-4">
        {displayedActivities.map((activity) => (
          <div key={activity.id} className="flex items-start space-x-3">
            <div className={`p-2 rounded-full flex-shrink-0 ${getActivityColor(activity.type)}`}>
              <ActivityIcon type={activity.type} />
            </div>
            
            <div className="flex-1 min-w-0">
              <div className="flex items-center justify-between">
                <p className="text-sm font-medium text-gray-900">
                  {activity.action}
                </p>
                <p className="text-xs text-gray-500 flex-shrink-0">
                  {activity.time.includes('ago') ? activity.time : formatTimeAgo(activity.time)}
                </p>
              </div>
              <p className="text-sm text-gray-600 mt-1">
                {activity.details}
              </p>
            </div>
          </div>
        ))}
      </div>
      
      {activities.length > maxItems && (
        <div className="mt-4 pt-4 border-t border-gray-200">
          <button className="w-full text-center text-sm text-blue-600 hover:text-blue-800 font-medium">
            View all {activities.length} activities
          </button>
        </div>
      )}
    </div>
  );
} 