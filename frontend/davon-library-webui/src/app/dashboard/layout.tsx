'use client';

import DashboardSidebar from '@/components/dashboard/DashboardSidebar';
import ProtectedRoute from '@/components/auth/ProtectedRoute';

export default function DashboardLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    // Hiçbir sayfada rol kontrolü yapmıyoruz, sadece kimlik doğrulama yeterli
    return (
        <ProtectedRoute>
            <div className="flex h-screen bg-gray-100">
                <DashboardSidebar />
                <main className="flex-1 overflow-y-auto">
                    {children}
                </main>
            </div>
        </ProtectedRoute>
    );
} 