'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { authService } from '@/lib/services/auth-service';
import { UserRole } from '@/lib/api/types';

export default function HomePage() {
    const router = useRouter();

    useEffect(() => {
        // Check if user is logged in
        const user = authService.getCurrentUser();
        
        if (user) {
            // Send all authenticated users to dashboard
            router.push('/dashboard');
        } else {
            // Redirect to login if not logged in
            router.push('/login');
        }
    }, [router]);

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="text-center">
                <h1 className="text-4xl font-bold text-gray-900 mb-4">
                    Davon Library System
                </h1>
                <p className="text-gray-600">
                    Redirecting to the appropriate page...
                </p>
            </div>
        </div>
    );
}
