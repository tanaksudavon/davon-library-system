'use client';

import { useEffect } from 'react';
import LoginForm from '@/components/auth/LoginForm';
import { authService } from '@/lib/services/auth-service';

export default function LoginPage() {
  useEffect(() => {
    // Clear auth data if we've been forcibly logged out
    if (typeof window !== 'undefined') {
      if (localStorage.getItem('forced_logout')) {
        console.log('[LoginPage] Forced logout detected, clearing auth data');
        authService.logout();
        localStorage.removeItem('forced_logout');
      }
    }
  }, []);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Sign in to your account
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Or{' '}
            <a href="/register" className="font-medium text-indigo-600 hover:text-indigo-500">
              create a new account
            </a>
          </p>
        </div>

        <LoginForm />
      </div>
    </div>
  );
} 