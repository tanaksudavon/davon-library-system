'use client';

import { User } from '@/types/user';

interface UserCardProps {
  user: User;
  onEdit?: () => void;
  onDelete?: () => void;
}

export default function UserCard({ user, onEdit, onDelete }: UserCardProps) {
  return (
    <div className="bg-white rounded-lg shadow-sm p-6">
      <div className="flex items-center space-x-4">
        <div className="h-16 w-16 bg-indigo-100 rounded-full flex items-center justify-center text-indigo-600 text-2xl">
          {user.username.charAt(0).toUpperCase()}
        </div>
        <div>
          <h3 className="text-lg font-medium text-gray-900">{user.username}</h3>
          <p className="text-sm text-gray-500">{user.email}</p>
        </div>
      </div>

      <div className="mt-4 space-y-2">
        <div className="flex items-center justify-between">
          <span className="text-sm text-gray-500">Role</span>
          <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
            user.role === 'admin' ? 'bg-green-100 text-green-800' : 'bg-blue-100 text-blue-800'
          }`}>
            {user.role}
          </span>
        </div>

        <div className="flex items-center justify-between">
          <span className="text-sm text-gray-500">Member Since</span>
          <span className="text-sm text-gray-900">
            {new Date(user.createdAt).toLocaleDateString()}
          </span>
        </div>

        <div className="flex items-center justify-between">
          <span className="text-sm text-gray-500">Last Updated</span>
          <span className="text-sm text-gray-900">
            {new Date(user.updatedAt).toLocaleDateString()}
          </span>
        </div>
      </div>

      {(onEdit || onDelete) && (
        <div className="mt-6 flex justify-end space-x-3">
          {onEdit && (
            <button
              onClick={onEdit}
              className="px-3 py-1 text-sm font-medium text-indigo-600 hover:text-indigo-900"
            >
              Edit
            </button>
          )}
          {onDelete && (
            <button
              onClick={onDelete}
              className="px-3 py-1 text-sm font-medium text-red-600 hover:text-red-900"
            >
              Delete
            </button>
          )}
        </div>
      )}
    </div>
  );
} 