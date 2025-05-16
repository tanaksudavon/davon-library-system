'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { authService } from '@/lib/services/auth-service';
import { useRouter } from 'next/navigation';
import { HomeIcon, BookOpenIcon, UsersIcon, UserIcon, BookmarkIcon } from '@heroicons/react/24/outline';
import { useEffect, useState } from 'react';

export default function DashboardSidebar() {
  const pathname = usePathname();
  const router = useRouter();
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    const currentUser = authService.getCurrentUser();
    setIsAdmin(currentUser?.role === 'admin');
  }, []);

  const handleLogout = () => {
    authService.logout();
    router.push('/login');
  };

  // Admin için tüm menüler
  const adminNavigation = [
    {
      name: 'Dashboard',
      href: '/dashboard',
      icon: <HomeIcon className="h-6 w-6" />,
      adminOnly: true
    },
    {
      name: 'Books',
      href: '/books',
      icon: <BookOpenIcon className="h-6 w-6" />,
      adminOnly: false
    },
    {
      name: 'Users',
      href: '/users',
      icon: <UsersIcon className="h-6 w-6" />,
      adminOnly: true
    },
    {
      name: 'Profile',
      href: '/profile',
      icon: <UserIcon className="h-6 w-6" />,
      adminOnly: false
    },
  ];

  // Kullanıcı rolüne göre navigasyon menüsünü filtrele
  const navigation = isAdmin 
    ? adminNavigation 
    : adminNavigation.filter(item => !item.adminOnly);

  useEffect(() => {
    // Debug için console'a yazdıralım
    console.log('[DashboardSidebar] Current user:', authService.getCurrentUser());
    console.log('[DashboardSidebar] Is admin:', isAdmin);
  }, [isAdmin]);

  const handleNavClick = (href: string) => {
    console.log('[DashboardSidebar] Navigation clicked:', href);
    console.log('[DashboardSidebar] Current user:', authService.getCurrentUser());
  };

  return (
    <div className="flex flex-col w-64 bg-white border-r">
      <div className="flex items-center justify-center h-16 border-b">
        <span className="text-xl font-semibold text-gray-800">Davon Library</span>
      </div>

      <nav className="flex-1 overflow-y-auto">
        <div className="px-2 py-4 space-y-1">
          {navigation.map((item) => {
            const isActive = pathname.startsWith(item.href);
            return (
              <Link
                key={item.name}
                href={item.href}
                onClick={() => handleNavClick(item.href)}
                className={`flex items-center px-4 py-2 text-sm font-medium rounded-md transition-colors ${
                  isActive
                    ? 'bg-primary-100 text-primary-900'
                    : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                }`}
              >
                <span className={`mr-3 ${isActive ? 'text-primary-500' : 'text-gray-400'}`}>
                  {item.icon}
                </span>
                {item.name}
              </Link>
            );
          })}
        </div>
      </nav>

      <div className="flex-shrink-0 p-4 border-t">
        <button
          onClick={handleLogout}
          className="flex items-center w-full px-4 py-2 text-sm font-medium text-gray-600 rounded-md hover:bg-gray-50 hover:text-gray-900"
        >
          <svg className="w-6 h-6 mr-3 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          Logout
        </button>
      </div>
    </div>
  );
} 