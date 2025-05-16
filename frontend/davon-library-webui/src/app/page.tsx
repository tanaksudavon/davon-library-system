'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/lib/store/auth-store';

export default function HomePage() {
    const router = useRouter();
    const { user } = useAuthStore();

    useEffect(() => {
        // Check if user is logged in
        if (user) {
            // Redirect based on user role
            if (user.role === 'admin') {
                router.push('/dashboard');
            } else {
                router.push('/profile');
            }
        } else {
            // Redirect to login if not logged in
            router.push('/login');
        }
    }, [user, router]);

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
