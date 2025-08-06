'use client';

import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/lib/store/auth-store';
import { UserProfileEdit } from '@/components/dashboard/UserProfileEdit';
import ProfileTabs from '@/components/profile/ProfileTabs';
import { FiLogOut } from 'react-icons/fi';

export default function ProfilePage() {
    const router = useRouter();
    const { user, clearAuth } = useAuthStore();



    const handleLogout = () => {
        clearAuth();
        router.push('/login');
    };

    return (
        <div className="container mx-auto px-4 py-8">
            <div className="max-w-4xl mx-auto">
                {/* Header */}
                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-gray-900">Profile</h1>
                    <p className="text-gray-600 mt-2">Manage your account settings and preferences</p>
                </div>

                {/* Profile Tabs */}
                <div className="bg-white rounded-lg shadow">
                    <ProfileTabs />

                    {/* Profile Content */}
                    <div className="p-6">
                        <UserProfileEdit />
                    </div>
                </div>

                {/* Logout Section */}
                <div className="mt-8 bg-white rounded-lg shadow p-6">
                    <h3 className="text-lg font-medium text-gray-900 mb-4">Account Actions</h3>
                    <button
                        onClick={handleLogout}
                        className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                    >
                        <FiLogOut className="w-4 h-4 mr-2" />
                        Logout
                    </button>
                </div>
            </div>
        </div>
    );
} 