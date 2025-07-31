'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { authService } from '@/lib/services/auth-service';

export default function LoginForm() {
  const router = useRouter();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [validCredentials, setValidCredentials] = useState<{username: string, password: string, role: string}[]>([]);

  // Get valid credentials for debugging purposes
  useEffect(() => {
    if (process.env.NODE_ENV === 'development') {
      try {
        const credentials = authService.getSampleCredentials();
        // Convert email-based credentials to username-based for display
        const usernameCredentials = [
          { username: 'admin', password: 'admin123', role: 'LIBRARIAN' },
          { username: 'sarah.jones', password: 'librarian123', role: 'LIBRARIAN' },
          { username: 'alice.wonder', password: 'member123', role: 'MEMBER' },
        ];
        setValidCredentials(usernameCredentials);
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
      console.log('Attempting to login with:', { username, password });
      const response = await authService.login({ username, password });
      console.log('Login successful:', response);
      
      if (response.role === 'LIBRARIAN') {
        router.push('/dashboard');
      } else {
        router.push('/dashboard/profile');
      }
    } catch (err) {
      console.error('Login error:', err);
      setError('Invalid username or password');
    } finally {
      setIsLoading(false);
    }
  };

  const handleClearAuth = () => {
    authService.clearAllAuthData();
    setUsername('');
    setPassword('');
    setError('');
    alert('Authentication data cleared. Please try logging in again.');
  };

  return (
    <form onSubmit={handleSubmit} className="mt-8 space-y-6">
      {process.env.NODE_ENV === 'development' && validCredentials.length > 0 && (
        <div className="text-sm p-3 bg-blue-50 rounded mb-4 border border-blue-200">
          <p className="font-bold mb-2 text-blue-800">🔧 Debug: Valid Login Credentials</p>
          {validCredentials.map((cred, i) => (
            <div key={i} className="mb-2 p-2 bg-white rounded border">
              <p className="font-medium">Username: <span className="text-blue-600">{cred.username}</span></p>
              <p className="font-medium">Password: <span className="text-blue-600">{cred.password}</span></p>
              <p className="text-xs text-gray-600">Role: {cred.role}</p>
            </div>
          ))}
        </div>
      )}

      <div className="-space-y-px">
        <div>
          <label htmlFor="username" className="sr-only">Username</label>
          <input
            id="username"
            name="username"
            type="text"
            autoComplete="username"
            required
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
            placeholder="Username"
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
          className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isLoading ? 'Signing in...' : 'Sign in'}
        </button>
      </div>

      {process.env.NODE_ENV === 'development' && (
        <div>
          <button
            type="button"
            onClick={handleClearAuth}
            className="group relative w-full flex justify-center py-2 px-4 border border-red-300 text-sm font-medium rounded-md text-red-700 bg-red-50 hover:bg-red-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
          >
            🗑️ Clear Auth Data (Debug)
          </button>
        </div>
      )}
    </form>
  );
}
