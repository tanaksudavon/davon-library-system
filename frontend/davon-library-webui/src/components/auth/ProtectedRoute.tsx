'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { authService } from '@/lib/services/auth-service';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: 'admin' | 'user';
}

export default function ProtectedRoute({ children, requiredRole }: ProtectedRouteProps) {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const checkAuth = () => {
      try {
        console.log('[ProtectedRoute] Checking auth...');
        
        const currentUser = authService.getCurrentUser();
        console.log('[ProtectedRoute] Current user:', currentUser);
        
        if (!currentUser) {
          console.log('[ProtectedRoute] No user found, redirecting to login');
          throw new Error('Not authenticated');
        }

        console.log('[ProtectedRoute] User authenticated');
        setIsLoading(false);
      } catch (error) {
        console.error('[ProtectedRoute] Auth error:', error);
        console.log('[ProtectedRoute] Redirecting to login page');
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