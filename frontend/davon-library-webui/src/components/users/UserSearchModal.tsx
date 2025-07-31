import { useState, useEffect, useCallback } from 'react';
import { User, UserRole } from '@/lib/api/types';
import { searchService } from '@/lib/api/services/search.service';
import { userService } from '@/lib/api/services/user.service';
import { FiX, FiSearch, FiUser, FiMail, FiPhone, FiCalendar, FiUserCheck, FiBookOpen } from 'react-icons/fi';

interface UserSearchModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSelectUser?: (user: User) => void;
  title?: string;
  allowSelection?: boolean;
}

export default function UserSearchModal({ 
  isOpen, 
  onClose, 
  onSelectUser,
  title = "Search Users",
  allowSelection = false 
}: UserSearchModalProps) {
  const [searchTerm, setSearchTerm] = useState('');
  const [users, setUsers] = useState<User[]>([]);
  const [allUsers, setAllUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [searchMode, setSearchMode] = useState<'search' | 'browse'>('browse');

  // Debounced search
  const [searchTimeout, setSearchTimeout] = useState<NodeJS.Timeout | null>(null);

  useEffect(() => {
    if (isOpen) {
      loadAllUsers();
      setSearchTerm('');
      setSelectedUser(null);
      setSearchMode('browse');
    }
  }, [isOpen]);

  const loadAllUsers = async () => {
    setLoading(true);
    try {
      const allUsersData = await userService.getAllUsers();
      setAllUsers(allUsersData);
      setUsers(allUsersData);
    } catch (error) {
      console.error('Failed to load users:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchChange = useCallback((value: string) => {
    setSearchTerm(value);
    
    // Clear previous timeout
    if (searchTimeout) {
      clearTimeout(searchTimeout);
    }

    if (value.trim().length === 0) {
      setUsers(allUsers);
      setSearchMode('browse');
      return;
    }

    if (value.trim().length < 2) {
      return;
    }

    setSearchMode('search');
    setLoading(true);

    // Set new timeout for debounced search
    const timeout = setTimeout(async () => {
      try {
        const searchResults = await searchService.searchUsers(value);
        setUsers(searchResults);
      } catch (error) {
        console.error('Failed to search users:', error);
        // Fallback to local search
        const localResults = allUsers.filter(user => 
          user.firstName.toLowerCase().includes(value.toLowerCase()) ||
          user.lastName.toLowerCase().includes(value.toLowerCase()) ||
          user.email.toLowerCase().includes(value.toLowerCase()) ||
          user.username?.toLowerCase().includes(value.toLowerCase())
        );
        setUsers(localResults);
      } finally {
        setLoading(false);
      }
    }, 300);

    setSearchTimeout(timeout);
  }, [allUsers, searchTimeout]);

  const handleUserSelect = (user: User) => {
    if (allowSelection) {
      setSelectedUser(user);
    }
  };

  const handleConfirmSelection = () => {
    if (selectedUser && onSelectUser) {
      onSelectUser(selectedUser);
      onClose();
    }
  };

  const getRoleColor = (role: UserRole) => {
    switch (role) {
      case UserRole.LIBRARIAN:
        return 'bg-purple-100 text-purple-800';
      case UserRole.MEMBER:
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800';
      case 'INACTIVE':
        return 'bg-red-100 text-red-800';
      case 'SUSPENDED':
        return 'bg-orange-100 text-orange-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b flex-shrink-0">
          <h2 className="text-xl font-semibold">{title}</h2>
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-100 rounded-full"
          >
            <FiX className="w-5 h-5" />
          </button>
        </div>

        {/* Search Bar */}
        <div className="p-6 border-b flex-shrink-0">
          <div className="relative">
            <FiSearch className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              type="text"
              placeholder="Search users by name, email, or username..."
              value={searchTerm}
              onChange={(e) => handleSearchChange(e.target.value)}
              className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>
          <div className="mt-2 flex items-center justify-between text-sm text-gray-600">
            <span>
              {searchMode === 'search' ? 
                `Search results for "${searchTerm}"` : 
                'Showing all users'
              } ({users.length} found)
            </span>
            {searchTerm && (
              <button
                onClick={() => {
                  setSearchTerm('');
                  setUsers(allUsers);
                  setSearchMode('browse');
                }}
                className="text-primary-600 hover:text-primary-700"
              >
                Clear search
              </button>
            )}
          </div>
        </div>

        {/* User List - Scrollable Content */}
        <div className="flex-1 overflow-y-auto min-h-0">
          <div className="p-6">
            {loading ? (
              <div className="flex items-center justify-center py-8">
                <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-primary-500"></div>
              </div>
            ) : users.length === 0 ? (
              <div className="text-center py-8">
                <FiUser className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-gray-900 mb-2">No Users Found</h3>
                <p className="text-gray-600">
                  {searchMode === 'search' ? 
                    'Try adjusting your search terms or browse all users.' :
                    'No users are currently registered in the system.'
                  }
                </p>
              </div>
            ) : (
              <div className="grid gap-4">
                {users.map((user) => (
                  <div
                    key={user.id}
                    onClick={() => handleUserSelect(user)}
                    className={`
                      border rounded-lg p-4 transition-all duration-200 
                      ${allowSelection ? 'cursor-pointer hover:shadow-md' : ''} 
                      ${selectedUser?.id === user.id ? 'ring-2 ring-primary-500 bg-primary-50' : 'hover:bg-gray-50'}
                    `}
                  >
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        {/* User Basic Info */}
                        <div className="flex items-center mb-3">
                          <div className="flex-shrink-0">
                            <div className="w-12 h-12 bg-gradient-to-br from-primary-500 to-primary-600 rounded-full flex items-center justify-center">
                              <FiUser className="w-6 h-6 text-white" />
                            </div>
                          </div>
                          <div className="ml-4 flex-1">
                            <div className="flex items-center space-x-3">
                              <h3 className="text-lg font-semibold text-gray-900">
                                {user.firstName} {user.lastName}
                              </h3>
                              <span className={`px-2 py-1 rounded-full text-xs font-medium ${getRoleColor(user.role)}`}>
                                {user.role}
                              </span>
                              <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(user.status)}`}>
                                {user.status}
                              </span>
                            </div>
                            <p className="text-sm text-gray-600 flex items-center mt-1">
                              <FiMail className="w-4 h-4 mr-1" />
                              {user.email}
                            </p>
                          </div>
                        </div>

                        {/* User Details */}
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm text-gray-600">
                          {user.username && (
                            <div className="flex items-center">
                              <FiUserCheck className="w-4 h-4 mr-2 text-gray-400" />
                              <span><strong>Username:</strong> {user.username}</span>
                            </div>
                          )}
                          {user.phoneNumber && (
                            <div className="flex items-center">
                              <FiPhone className="w-4 h-4 mr-2 text-gray-400" />
                              <span><strong>Phone:</strong> {user.phoneNumber}</span>
                            </div>
                          )}
                          {user.dateOfBirth && (
                            <div className="flex items-center">
                              <FiCalendar className="w-4 h-4 mr-2 text-gray-400" />
                              <span><strong>DOB:</strong> {new Date(user.dateOfBirth).toLocaleDateString()}</span>
                            </div>
                          )}
                        </div>

                        {/* User Statistics - This would need to be fetched separately */}
                        {/* Future enhancement: Add loan statistics via a separate API call */}
                      </div>

                      {allowSelection && selectedUser?.id === user.id && (
                        <div className="ml-4">
                          <div className="w-6 h-6 bg-primary-500 rounded-full flex items-center justify-center">
                            <FiUserCheck className="w-4 h-4 text-white" />
                          </div>
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Footer */}
        {allowSelection && (
          <div className="p-6 border-t bg-gray-50 flex-shrink-0">
            <div className="flex justify-between items-center">
              <div className="text-sm text-gray-600">
                {selectedUser ? (
                  <span>Selected: <strong>{selectedUser.firstName} {selectedUser.lastName}</strong></span>
                ) : (
                  <span>Select a user to continue</span>
                )}
              </div>
              <div className="flex space-x-3">
                <button
                  onClick={onClose}
                  className="px-4 py-2 text-gray-700 bg-gray-200 rounded-lg hover:bg-gray-300"
                >
                  Cancel
                </button>
                <button
                  onClick={handleConfirmSelection}
                  disabled={!selectedUser}
                  className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Select User
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
} 