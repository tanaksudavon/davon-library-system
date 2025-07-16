'use client';

import UserList from '@/components/users/UserList';
import AdminRoute from '@/components/auth/AdminRoute';

function UsersContent() {
  return (
    <div className="space-y-6">
      <div className="sm:flex sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-gray-900">Users</h1>
          <p className="mt-2 text-sm text-gray-700">
            A list of all users in your library system.
          </p>
        </div>
      </div>

      <UserList />
    </div>
  );
}

export default function UsersPage() {
  return (
    <AdminRoute>
      <UsersContent />
    </AdminRoute>
  );
} 