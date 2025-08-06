'use client';

import DashboardSidebar from '@/components/dashboard/DashboardSidebar';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import NotificationBell from '@/components/notifications/NotificationBell';
import { authService } from '@/lib/services/auth-service';
import { useState, useEffect } from 'react';
import { usePathname } from 'next/navigation';

export default function DashboardLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    const [currentUser, setCurrentUser] = useState<any>(null);
    const pathname = usePathname();
    
    // Only show notification bell on the main dashboard page
    const showNotificationBell = pathname === '/dashboard/dashboard';

    useEffect(() => {
        const user = authService.getCurrentUser();
        setCurrentUser(user);
    }, []);

    return (
        <ProtectedRoute>
            <div className="flex h-screen bg-gray-100">
                <DashboardSidebar />
                <div className="flex-1 flex flex-col">
                    {/* Header */}
                    <header className="bg-white shadow-sm border-b border-gray-200 px-6 py-4">
                        <div className="flex items-center justify-between">
                            <div>
                                <h1 className="text-xl font-semibold text-gray-900">
                                    Davon Library System
                                </h1>
                            </div>
                            <div className="flex items-center space-x-4">
                                {currentUser && (
                                    <>
                                        {showNotificationBell && (
                                            <NotificationBell userId={currentUser.id} />
                                        )}
                                        <div className="flex items-center space-x-2">
                                            <div className="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                                                <span className="text-white text-sm font-medium">
                                                    {currentUser.firstName?.charAt(0) || 'U'}
                                                </span>
                                            </div>
                                            <span className="text-sm text-gray-700">
                                                {currentUser.firstName} {currentUser.lastName}
                                            </span>
                                        </div>
                                    </>
                                )}
                            </div>
                        </div>
                    </header>
                    
                    {/* Main Content */}
                    <main className="flex-1 overflow-y-auto p-6">
                        {children}
                    </main>
                </div>
            </div>
        </ProtectedRoute>
    );
} 