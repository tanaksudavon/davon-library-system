'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { authService } from '@/lib/services/auth-service';

// Enable this for development when facing auth issues
const BYPASS_AUTH_FOR_DEVELOPMENT = true;

interface AdminRouteProps {
  children: React.ReactNode;
}

export default function AdminRoute({ children }: AdminRouteProps) {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(true);
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    const checkAuth = () => {
      try {
        console.log('[AdminRoute] Checking admin auth...');
        
        // DEVELOPMENT BYPASS - remove in production
        if (BYPASS_AUTH_FOR_DEVELOPMENT) {
          console.log('[AdminRoute] ⚠️ USING DEV AUTH BYPASS - NOT FOR PRODUCTION ⚠️');
          setIsAdmin(true);
          setIsLoading(false);
          return;
        }
        
        // For debugging - log all localStorage keys and values
        if (typeof window !== 'undefined') {
          console.log('[AdminRoute] All localStorage items:');
          for (let i = 0; i < localStorage.length; i++) {
            const key = localStorage.key(i);
            if (key) {
              console.log(`${key}: ${localStorage.getItem(key)}`);
            }
          }
        }
        
        const currentUser = authService.getCurrentUser();
        console.log('[AdminRoute] Current user:', currentUser);
        
        if (!currentUser) {
          console.log('[AdminRoute] No user found, redirecting to login');
          router.push('/login');
          return;
        }

        if (currentUser.role !== 'admin') {
          console.log('[AdminRoute] User not admin, redirecting to profile');
          router.push('/profile');
          return;
        }

        console.log('[AdminRoute] User is admin, continuing');
        setIsAdmin(true);
        setIsLoading(false);
      } catch (error) {
        console.error('[AdminRoute] Auth error:', error);
        console.log('[AdminRoute] Redirecting to login page');
        router.push('/login');
      }
    };

    checkAuth();
    
    // Add an interval to periodically check authentication status
    const interval = setInterval(() => {
      // Skip auth checks if using bypass
      if (BYPASS_AUTH_FOR_DEVELOPMENT) return;
      
      const currentUser = authService.getCurrentUser();
      if (!currentUser || currentUser.role !== 'admin') {
        console.log('[AdminRoute] Auth check failed in interval');
        clearInterval(interval);
        router.push('/login');
      }
    }, 10000); // Check every 10 seconds
    
    return () => {
      clearInterval(interval);
    };
  }, [router]);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-500"></div>
      </div>
    );
  }

  if (!isAdmin) {
    return null;
  }

  return <>{children}</>;
} 