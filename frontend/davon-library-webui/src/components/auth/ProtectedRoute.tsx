'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { authService } from '@/lib/services/auth-service';

// Enable this for development when facing auth issues
const BYPASS_AUTH_FOR_DEVELOPMENT = true;

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: 'admin' | 'user';
}

export default function ProtectedRoute({ children, requiredRole }: ProtectedRouteProps) {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const checkAuth = () => {
      try {
        console.log('[ProtectedRoute] Checking auth...');
        
        // DEVELOPMENT BYPASS - remove in production
        if (BYPASS_AUTH_FOR_DEVELOPMENT) {
          console.log('[ProtectedRoute] ⚠️ USING DEV AUTH BYPASS - NOT FOR PRODUCTION ⚠️');
          setIsAuthenticated(true);
          setIsLoading(false);
          return;
        }
        
        // For debugging - log all localStorage keys and values
        if (typeof window !== 'undefined') {
          console.log('[ProtectedRoute] All localStorage items:');
          for (let i = 0; i < localStorage.length; i++) {
            const key = localStorage.key(i);
            if (key) {
              console.log(`${key}: ${localStorage.getItem(key)}`);
            }
          }
        }
        
        const currentUser = authService.getCurrentUser();
        console.log('[ProtectedRoute] Current user:', currentUser);
        
        if (!currentUser) {
          console.log('[ProtectedRoute] No user found, redirecting to login');
          router.push('/login');
          return;
        }

        // Check role if required
        if (requiredRole && currentUser.role !== requiredRole) {
          console.log(`[ProtectedRoute] User role ${currentUser.role} doesn't match required role ${requiredRole}`);
          router.push('/profile');
          return;
        }

        console.log('[ProtectedRoute] User authenticated');
        setIsAuthenticated(true);
        setIsLoading(false);
      } catch (error) {
        console.error('[ProtectedRoute] Auth error:', error);
        console.log('[ProtectedRoute] Redirecting to login page');
        router.push('/login');
      }
    };

    checkAuth();
  }, [router, requiredRole]);

  // Show loading state
  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-500"></div>
      </div>
    );
  }

  // Only render children if authenticated
  if (!isAuthenticated) {
    return null;
  }

  return <>{children}</>;
} 