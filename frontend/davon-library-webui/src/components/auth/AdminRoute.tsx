'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { authService } from '@/lib/services/auth-service';
import { UserRole } from '@/lib/api/types';

interface AdminRouteProps {
  children: React.ReactNode;
}

export default function AdminRoute({ children }: AdminRouteProps) {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const checkAuth = () => {
      try {
        console.log('[AdminRoute] Checking admin auth...');
        
        const currentUser = authService.getCurrentUser();
        console.log('[AdminRoute] Current user:', currentUser);
        
        if (!currentUser) {
          console.log('[AdminRoute] No user found, redirecting to login');
          throw new Error('Not authenticated');
        }

        if (currentUser.role !== UserRole.LIBRARIAN) {
          console.log('[AdminRoute] User not librarian, redirecting to dashboard');
          router.push('/dashboard/dashboard');
          return;
        }

        console.log('[AdminRoute] User is librarian');
        setIsLoading(false);
      } catch (error) {
        console.error('[AdminRoute] Auth error:', error);
        console.log('[AdminRoute] Redirecting to login page');
        router.push('/login');
      }
    };

    checkAuth();
  }, [router]);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-500"></div>
      </div>
    );
  }

  return <>{children}</>;
} 