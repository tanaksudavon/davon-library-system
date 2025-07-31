'use client';

import { usePathname, useRouter } from 'next/navigation';
import { FiUser, FiBookOpen, FiClock } from 'react-icons/fi';

const tabs = [
  { 
    label: 'Profile Information', 
    path: '/dashboard/profile',
    icon: FiUser
  },
  { 
    label: 'My Books', 
    path: '/dashboard/profile/books',
    icon: FiBookOpen
  },
  { 
    label: 'History', 
    path: '/dashboard/profile/history',
    icon: FiClock
  },
];

export default function ProfileTabs() {
  const pathname = usePathname();
  const router = useRouter();

  return (
    <div className="border-b border-gray-200">
      <nav className="-mb-px flex space-x-8 px-6">
        {tabs.map(tab => {
          const Icon = tab.icon;
          const isActive = pathname === tab.path;
          
          return (
            <button
              key={tab.path}
              className={`whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm inline-flex items-center ${
                isActive
                  ? 'border-primary-500 text-primary-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
              onClick={() => router.push(tab.path)}
            >
              <Icon className="w-4 h-4 mr-2" />
              {tab.label}
            </button>
          );
        })}
      </nav>
    </div>
  );
} 