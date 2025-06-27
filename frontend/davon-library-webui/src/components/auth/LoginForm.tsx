'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { authService } from '@/lib/services/auth-service';

export default function LoginForm() {
  const router = useRouter();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [validCredentials, setValidCredentials] = useState<{email: string, password: string}[]>([]);

  // Get valid credentials for debugging purposes
  useEffect(() => {
    if (process.env.NODE_ENV === 'development') {
      try {
        const credentials = authService.getValidCredentials();
        setValidCredentials(credentials);
      } catch (err) {
        console.error('Could not fetch valid credentials:', err);
      }
    }
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      console.log('Attempting to login with:', { email, password });
      const response = await authService.login(email, password);
      console.log('Login successful:', response);
      
      if (response.user.role === 'admin') {
        router.push('/dashboard');
      } else {
        router.push('/profile');
      }
    } catch (err) {
      console.error('Login error:', err);
      setError('Invalid email or password');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="mt-8 space-y-6">
      {process.env.NODE_ENV === 'development' && validCredentials.length > 0 && (
        <div className="text-sm p-3 bg-gray-100 rounded mb-4">
          <p className="font-bold mb-1">Debug: Valid Credentials</p>
          {validCredentials.map((cred, i) => (
            <div key={i} className="mb-1">
              <p>Email: {cred.email}</p>
              <p>Password: {cred.password}</p>
            </div>
          ))}
        </div>
      )}

      <div className="-space-y-px">
        <div>
          <label htmlFor="email" className="sr-only">Email</label>
          <input
            id="email"
            name="email"
            type="email"
            autoComplete="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
            placeholder="Email address"
          />
        </div>
        <div>
          <label htmlFor="password" className="sr-only">Password</label>
          <input
            id="password"
            name="password"
            type="password"
            autoComplete="current-password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
            placeholder="Password"
          />
        </div>
      </div>

      {error && (
        <div className="text-sm text-red-600 bg-red-50 p-2 rounded">
          {error}
        </div>
      )}

      <div>
        <button
          type="submit"
          disabled={isLoading}
          className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          {isLoading ? 'Signing in...' : 'Sign in'}
        </button>
      </div>
    </form>
  );
}
