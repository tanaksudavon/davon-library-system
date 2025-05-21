'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { authService } from '@/lib/services/auth-service';

interface LoginFormData {
  email: string;
  password: string;
}

export default function LoginForm() {
  const router = useRouter();
  const [formData, setFormData] = useState<LoginFormData>({
    email: '',
    password: '',
  });
  const [error, setError] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    console.log('🟢 LoginForm MOUNT oldu');
    
    // Clear any existing auth data to start fresh
    if (typeof window !== 'undefined') {
      // Don't clear during development to make testing easier
      // localStorage.removeItem('user');
      // localStorage.removeItem('token');
      
      // Check if we already have valid auth data
      const currentUser = authService.getCurrentUser();
      if (currentUser) {
        console.log('User already logged in:', currentUser);
        if (currentUser.role === 'admin') {
          router.push('/dashboard');
        } else {
          router.push('/profile');
        }
      }
    }

    return () => {
      console.log('🔴 LoginForm UNMOUNT oluyor');
    };
  }, [router]);

  useEffect(() => {
    console.log('🔄 LoginForm UPDATE: formData değişti', formData);
  }, [formData]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      console.log('Attempting login with:', formData);
      const response = await authService.login(formData.email, formData.password);
      console.log('Login successful, response:', response);
      
      // Double check localStorage was set correctly
      if (typeof window !== 'undefined') {
        console.log('LocalStorage after login:');
        console.log('- user:', localStorage.getItem('user'));
        console.log('- token:', localStorage.getItem('token'));
      }
      
      // Wait a moment to ensure local storage is updated
      setTimeout(() => {
        if (response.user.role === 'admin') {
          router.push('/dashboard');
        } else {
          router.push('/profile');
        }
      }, 100);
    } catch (err) {
      console.error('Login error:', err);
      setError('Invalid email or password');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="w-full max-w-md mx-auto p-6">
      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label htmlFor="email" className="block text-sm font-medium text-gray-700">
            Email
          </label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
          />
        </div>

        <div>
          <label htmlFor="password" className="block text-sm font-medium text-gray-700">
            Password
          </label>
          <input
            type="password"
            id="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
          />
        </div>

        {error && (
          <div className="text-red-500 text-sm">{error}</div>
        )}

        <button
          type="submit"
          disabled={isLoading}
          className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
        >
          {isLoading ? 'Signing in...' : 'Sign in'}
        </button>
      </form>
    </div>
  );
}
