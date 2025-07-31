'use client';

import { useState } from 'react';
import UserList from '@/components/users/UserList';
import UserSearchModal from '@/components/users/UserSearchModal';
import AdminRoute from '@/components/auth/AdminRoute';
import { FiSearch } from 'react-icons/fi';

function UsersContent() {
  const [isSearchModalOpen, setIsSearchModalOpen] = useState(false);

  const handleOpenSearch = () => {
    setIsSearchModalOpen(true);
  };

  const handleCloseSearch = () => {
    setIsSearchModalOpen(false);
  };

  return (
    <div className="space-y-6">
      <div className="sm:flex sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-gray-900">Users</h1>
          <p className="mt-2 text-sm text-gray-700">
            A list of all users in your library system.
          </p>
        </div>
        <div className="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
          <button
            type="button"
            onClick={handleOpenSearch}
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            <FiSearch className="mr-2 h-4 w-4" />
            Search Users
          </button>
        </div>
      </div>

      <UserList />

      {/* User Search Modal */}
      <UserSearchModal
        isOpen={isSearchModalOpen}
        onClose={handleCloseSearch}
        title="Search Users"
        allowSelection={false}
      />
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