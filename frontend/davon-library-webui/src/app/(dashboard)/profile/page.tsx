'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/lib/store/auth-store';
import { authService } from '@/lib/services/auth-service';
import { UserProfileEdit } from '@/components/dashboard/UserProfileEdit';
import { AuthState } from '@/types/auth';
import Link from 'next/link';
import { FiUser, FiKey, FiLogOut } from 'react-icons/fi';
import { MOCK_ADMIN_USER } from '@/lib/services/mockUser';

// Enable this for development when facing auth issues
// Set to false if you want normal authentication behavior
const BYPASS_AUTH_FOR_DEVELOPMENT = false;

export default function ProfilePage() {
    const router = useRouter();
    const { user, setAuth, clearAuth } = useAuthStore((state: AuthState) => state);
    const [isLoading, setIsLoading] = useState(true);
    const [authChecked, setAuthChecked] = useState(false);
    const [devMode, setDevMode] = useState(false);
    // Track if we're redirecting to login to prevent loops
    const [isRedirectingToLogin, setIsRedirectingToLogin] = useState(false);

    useEffect(() => {
        // Only run this once
        if (authChecked) return;
        
        const initializeAuth = async () => {
            setIsLoading(true);
            
            // DEVELOPMENT BYPASS
            if (BYPASS_AUTH_FOR_DEVELOPMENT) {
                console.log('[ProfilePage] ⚠️ DEV MODE ENABLED - Bypassing authentication ⚠️');
                setAuth(MOCK_ADMIN_USER, 'dev-token-12345');
                setDevMode(true);
                setAuthChecked(true);
                setIsLoading(false);
                return;
            }
            
            try {
                console.log('[ProfilePage] Checking authentication...');
                
                // Check if we just logged in successfully (flag from login page)
                const hasLoginSuccess = localStorage.getItem('login_success');
                if (hasLoginSuccess) {
                    console.log('[ProfilePage] Recent login detected, no need to redirect');
                    localStorage.removeItem('login_success'); // Clear flag after use
                    setAuthChecked(true);
                    setIsLoading(false);
                    return;
                }
                
                // Check if we're already in a redirect-to-login process
                if (localStorage.getItem('redirecting_to_login')) {
                    console.log('[ProfilePage] Already redirecting to login, stopping to prevent loop');
                    setAuthChecked(true);
                    setIsRedirectingToLogin(true);
                    setIsLoading(false);
                    return;
                }
                
                // Normal auth check flow
                const token = authService.getToken();
                
                if (!token) {
                    console.log('[ProfilePage] No token found, preparing to redirect');
                    // Set flag to prevent redirect loops
                    localStorage.setItem('redirecting_to_login', 'true');
                    setIsRedirectingToLogin(true);
                    setAuthChecked(true);
                    setIsLoading(false);
                    return;
                }
                
                console.log('[ProfilePage] Token found');
                const currentUser = authService.getCurrentUser();
                console.log('[ProfilePage] Current user:', currentUser);
                
                if (!currentUser) {
                    console.log('[ProfilePage] No user found with token, preparing to redirect');
                    // Set flag to prevent redirect loops
                    localStorage.setItem('redirecting_to_login', 'true');
                    localStorage.removeItem('token'); // Clear invalid token
                    localStorage.removeItem('user'); // Also remove user data
                    setIsRedirectingToLogin(true);
                    setAuthChecked(true);
                    setIsLoading(false);
                    return;
                }
                
                console.log('[ProfilePage] Setting auth with user');
                setAuth(currentUser, token);
                setAuthChecked(true);
                setIsLoading(false);
            } catch (error) {
                console.error('[ProfilePage] Error initializing auth:', error);
                // Set flag to prevent redirect loops
                localStorage.setItem('redirecting_to_login', 'true');
                localStorage.removeItem('token'); // Clear potentially corrupt token
                localStorage.removeItem('user'); // Also remove user data
                setIsRedirectingToLogin(true);
                setAuthChecked(true);
                setIsLoading(false);
            }
        };

        initializeAuth();
    }, [router, setAuth, authChecked]);

    // Execute redirect in a separate effect to ensure it runs after auth check
    useEffect(() => {
        if (isRedirectingToLogin && !BYPASS_AUTH_FOR_DEVELOPMENT) {
            console.log('[ProfilePage] Redirecting to login');
            // Mark as forced logout to prevent auto-redirect back
            localStorage.setItem('forced_logout', 'true');
            // Reset redirect loop count
            localStorage.removeItem('redirect_loop_count');
            // Small delay to ensure everything is set
            setTimeout(() => {
                router.replace('/login');
            }, 100);
        }
    }, [isRedirectingToLogin, router]);

    const handleLogout = () => {
        // Even in dev mode, we should allow logout
        clearAuth();
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('login_success');
        localStorage.removeItem('redirecting_to_login');
        localStorage.removeItem('redirect_loop_count');
        localStorage.setItem('forced_logout', 'true');
        
        // Force a refresh to ensure state is cleared
        window.location.href = '/login';
    };

    if (isLoading) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <div className="animate-pulse flex flex-col items-center">
                    <div className="h-12 w-12 bg-blue-400 rounded-full mb-4"></div>
                    <div className="h-4 w-24 bg-slate-200 rounded mb-2.5"></div>
                </div>
            </div>
        );
    }

    if (isRedirectingToLogin && !BYPASS_AUTH_FOR_DEVELOPMENT) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <div className="text-center">
                    <h2 className="text-xl font-semibold">Authentication required</h2>
                    <p className="mt-2">Redirecting to login page...</p>
                </div>
            </div>
        );
    }

    // Always use the user from context or mock user in dev mode
    const displayUser = user || MOCK_ADMIN_USER;

    if (devMode) {
        console.log('[ProfilePage] ⚠️ Rendering in DEV MODE with mock user ⚠️');
    }

    return (
        <div className="min-h-screen bg-slate-50">
            {devMode && (
                <div className="bg-yellow-100 text-yellow-800 p-1 text-center text-xs">
                    Development Mode
                </div>
            )}
            <div className="py-4 px-6 bg-white shadow-sm flex justify-between items-center">
                <div className="text-xl font-bold text-slate-800">Davon Library</div>
                <div className="flex items-center gap-4">
                    {displayUser.role === 'admin' && (
                        <Link href="/dashboard" className="text-blue-600 hover:underline">
                            Admin Dashboard
                        </Link>
                    )}
                    <div className="flex items-center">
                        <div className="mr-2">
                            <span className="text-sm font-medium">{displayUser.username}</span>
                            <span className="text-xs block text-slate-500">{displayUser.role}</span>
                        </div>
                        <div className="h-8 w-8 bg-blue-100 rounded-full flex items-center justify-center text-blue-600">
                            {displayUser.username.charAt(0).toUpperCase()}
                        </div>
                    </div>
                </div>
            </div>
            
            <div className="container mx-auto py-8 px-4">
                <div className="flex flex-col md:flex-row gap-6">
                    <div className="w-full md:w-1/4">
                        <div className="bg-white shadow-sm rounded-lg p-6">
                            <div className="text-center mb-6">
                                <div className="h-24 w-24 bg-blue-100 rounded-full flex items-center justify-center text-blue-600 text-3xl mx-auto mb-4">
                                    {displayUser.username.charAt(0).toUpperCase()}
                                </div>
                                <h2 className="text-xl font-semibold">{displayUser.username}</h2>
                                <p className="text-sm text-slate-500">{displayUser.email}</p>
                                <div className="mt-2">
                                    <span className={`text-xs px-2 py-1 rounded-full ${displayUser.role === 'admin' ? 'bg-green-100 text-green-800' : 'bg-blue-100 text-blue-800'}`}>
                                        {displayUser.role}
                                    </span>
                                </div>
                            </div>
                            
                            <div className="space-y-2">
                                <button className="flex items-center w-full py-2 px-3 text-left rounded hover:bg-blue-50 text-blue-700">
                                    <FiUser className="mr-2" /> My Profile
                                </button>
                                <button className="flex items-center w-full py-2 px-3 text-left rounded hover:bg-blue-50">
                                    <FiKey className="mr-2" /> Change Password
                                </button>
                                <button onClick={handleLogout} className="flex items-center w-full py-2 px-3 text-left rounded hover:bg-red-50 text-red-600">
                                    <FiLogOut className="mr-2" /> Log Out
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <div className="w-full md:w-3/4">
                        <div className="bg-white shadow-sm rounded-lg p-6">
                            <h1 className="text-xl font-bold text-slate-800 mb-6">My Profile</h1>
                            <UserProfileEdit />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
} 