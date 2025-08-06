'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/lib/store/auth-store';
import { FiClock, FiCheck, FiX, FiDollarSign, FiAlertCircle, FiRefreshCw } from 'react-icons/fi';
import { Loan, Fine, LoanStatus, FineStatus } from '@/lib/api/types';
import { userProfileService } from '@/lib/api/services/user-profile.service';
import { fineService } from '@/lib/api/services/fine.service';
import ProfileTabs from '@/components/profile/ProfileTabs';
import { toast } from 'react-hot-toast';

interface LoanWithFine extends Loan {
    fine?: Fine;
}

export default function UserHistoryPage() {
    const router = useRouter();
    const { user } = useAuthStore();
    const [loanHistory, setLoanHistory] = useState<LoanWithFine[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [totalFines, setTotalFines] = useState<number>(0);
    const [isRefreshingFines, setIsRefreshingFines] = useState(false);

    useEffect(() => {
        if (!user) {
            router.replace('/login');
            return;
        }
        loadLoanHistory();
    }, [user, router]);

    const loadLoanHistory = async () => {
        if (!user) return;
        
        setIsLoading(true);
        setError(null);
        
        try {
            const [loanHistoryData, userTotalFines, userFines] = await Promise.all([
                userProfileService.getUserLoanHistory(Number(user.id)),
                fineService.getUserTotalFines(Number(user.id)),
                fineService.getFinesByUserId(Number(user.id))
            ]);
            
            const enhancedLoans: LoanWithFine[] = loanHistoryData.map(loan => {
                const matchingFine = userFines.find(f => f.loan?.id === loan.id);
                return matchingFine ? { ...loan, fine: matchingFine } : loan;
            });
            
            setLoanHistory(enhancedLoans);
            setTotalFines(userTotalFines);
        } catch (err) {
            console.error('Failed to load loan history:', err);
            setError('Failed to load your loan history. Please try again.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleRefreshFines = async () => {
        setIsRefreshingFines(true);
        setError(null);

        try {
            const result = await fineService.calculateOverdueFines();
            toast.success(result.message || 'Fines have been successfully updated.');
            await loadLoanHistory(); // Refresh the entire history view
        } catch (err: any) {
            console.error('Failed to refresh fines:', err);
            const errorMessage = err?.response?.data?.error || err?.message || 'Failed to update fines. Please try again.';
            setError(errorMessage);
            toast.error(errorMessage);
        } finally {
            setIsRefreshingFines(false);
        }
    };

    const getStatusIcon = (loan: LoanWithFine) => {
        if (loan.returnDate) {
            return <FiCheck className="mr-1 w-4 h-4" />;
        }
        
        const isOverdue = loan.dueDate && new Date(loan.dueDate) < new Date();
        if (isOverdue) {
            return <FiX className="mr-1 w-4 h-4" />;
        }
        
        return <FiClock className="mr-1 w-4 h-4" />;
    };

    const getStatusColor = (loan: LoanWithFine) => {
        if (loan.returnDate) {
            return 'bg-green-100 text-green-800';
        }
        
        const isOverdue = loan.dueDate && new Date(loan.dueDate) < new Date();
        if (isOverdue) {
            return 'bg-red-100 text-red-800';
        }
        
        return 'bg-blue-100 text-blue-800';
    };

    const getStatusText = (loan: LoanWithFine) => {
        if (loan.returnDate) {
            return 'Returned';
        }
        
        const isOverdue = loan.dueDate && new Date(loan.dueDate) < new Date();
        if (isOverdue) {
            return 'Overdue';
        }
        
        return 'Borrowed';
    };

    const handlePayFine = async (fineId: number) => {
        try {
            const result = await fineService.payFine(fineId);
            console.log('Fine payment successful:', result);
            toast.success('Fine paid successfully!');
            await loadLoanHistory(); // Refresh the data
        } catch (err: any) {
            console.error('Failed to pay fine:', err);
            const errorMessage = err?.response?.data?.error || err?.message || 'Failed to pay fine. Please try again.';
            setError(errorMessage);
            toast.error(errorMessage);
        }
    };

    const handlePayAllFines = async () => {
        if (!user) return;
        try {
            const pendingFines = await fineService.getPendingFinesByUserId(Number(user.id));
            for (const fine of pendingFines) {
                await fineService.payFine(fine.id);
            }
            toast.success('All fines paid successfully!');
            await loadLoanHistory();
        } catch (err: any) {
            console.error('Failed to pay fines:', err);
            const errorMessage = err?.response?.data?.error || err?.message || 'Failed to pay fines. Please try again.';
            setError(errorMessage);
            toast.error(errorMessage);
        }
    };

    if (!user) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <div className="text-center">
                    <h2 className="text-xl font-semibold">Authentication required</h2>
                    <p className="mt-2">Please log in to view your loan history</p>
                </div>
            </div>
        );
    }

    if (isLoading) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-500 mx-auto"></div>
                    <p className="mt-4 text-gray-600">Loading your loan history...</p>
                </div>
            </div>
        );
    }

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

                    {/* History Content */}
                    <div className="p-6">
                        <div className="flex justify-between items-center mb-6">
                            <h2 className="text-2xl font-bold text-slate-800">Loan History</h2>
                            <button
                                onClick={handleRefreshFines}
                                disabled={isRefreshingFines}
                                className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-blue-300 transition-colors"
                            >
                                <FiRefreshCw className={`mr-2 ${isRefreshingFines ? 'animate-spin' : ''}`} />
                                {isRefreshingFines ? 'Refreshing...' : 'Refresh Fines'}
                            </button>
                        </div>
                        
                        {error && (
                            <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-md">
                                <div className="flex">
                                    <FiAlertCircle className="h-5 w-5 text-red-400 mt-0.5 mr-3" />
                                    <p className="text-red-700">{error}</p>
                                </div>
                            </div>
                        )}

                        {/* Outstanding Fines Summary */}
                        {totalFines > 0 && (
                            <div className="mb-6 p-4 bg-yellow-50 border border-yellow-200 rounded-md">
                                <div className="flex items-center justify-between">
                                    <div className="flex items-center">
                                        <FiDollarSign className="h-5 w-5 text-yellow-400 mr-3" />
                                        <div>
                                            <h3 className="text-sm font-medium text-yellow-800">Outstanding Fines</h3>
                                            <p className="text-sm text-yellow-700">
                                                You have ${totalFines.toFixed(2)} in outstanding fines.
                                            </p>
                                        </div>
                                    </div>
                                    <button
                                        onClick={handlePayAllFines}
                                        className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
                                    >
                                        Pay Now
                                    </button>
                                </div>
                            </div>
                        )}
                        
                        <div className="bg-white shadow-sm rounded-lg overflow-hidden">
                            <div className="overflow-x-auto">
                                <table className="min-w-full divide-y divide-gray-200">
                                    <thead className="bg-gray-50">
                                        <tr>
                                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                                Book
                                            </th>
                                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                                Borrow Date
                                            </th>
                                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                                Return/Due Date
                                            </th>
                                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                                Status
                                            </th>
                                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                                Fine
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody className="bg-white divide-y divide-gray-200">
                                        {loanHistory.length > 0 ? (
                                            loanHistory.map((loan) => (
                                                <tr key={loan.id}>
                                                    <td className="px-6 py-4 whitespace-nowrap">
                                                        <div className="text-sm font-medium text-gray-900">
                                                            {loan.book?.title || 'Unknown Book'}
                                                        </div>
                                                        <div className="text-sm text-gray-500">
                                                            {loan.book?.author ? 
                                                                `${loan.book.author.firstName} ${loan.book.author.lastName}` : 
                                                                'Unknown Author'
                                                            }
                                                        </div>
                                                    </td>
                                                    <td className="px-6 py-4 whitespace-nowrap">
                                                        <div className="text-sm text-gray-900">
                                                            {loan.borrowDate ? new Date(loan.borrowDate).toLocaleDateString() : 'N/A'}
                                                        </div>
                                                    </td>
                                                    <td className="px-6 py-4 whitespace-nowrap">
                                                        <div className="text-sm text-gray-900">
                                                            {loan.returnDate
                                                                ? new Date(loan.returnDate).toLocaleDateString()
                                                                : loan.dueDate ? new Date(loan.dueDate).toLocaleDateString() : 'N/A'}
                                                        </div>
                                                    </td>
                                                    <td className="px-6 py-4 whitespace-nowrap">
                                                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(loan)}`}>
                                                            {getStatusIcon(loan)}
                                                            {getStatusText(loan)}
                                                        </span>
                                                    </td>
                                                    <td className="px-6 py-4 whitespace-nowrap">
                                                        {loan.fine ? (
                                                            <div className="flex items-center space-x-2">
                                                                <span className={`text-sm font-medium ${loan.fine.status === FineStatus.PAID ? 'text-green-600' : 'text-red-600'}`}>
                                                                    ${loan.fine.amount.toFixed(2)}
                                                                </span>
                                                                {loan.fine.status === FineStatus.PENDING && (
                                                                    <button
                                                                        onClick={() => handlePayFine(loan.fine!.id)}
                                                                        className="text-xs bg-blue-600 hover:bg-blue-700 text-white px-2 py-1 rounded"
                                                                    >
                                                                        Pay
                                                                    </button>
                                                                )}
                                                                {loan.fine.status === FineStatus.PAID && (
                                                                    <span className="text-xs text-green-600 font-medium">Paid</span>
                                                                )}
                                                            </div>
                                                        ) : (
                                                            <span className="text-sm text-gray-500">No fine</span>
                                                        )}
                                                    </td>
                                                </tr>
                                            ))
                                        ) : (
                                            <tr>
                                                <td colSpan={5} className="px-6 py-4 whitespace-nowrap text-center text-sm text-gray-500">
                                                    No loan history found.
                                                </td>
                                            </tr>
                                        )}
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        {/* Library Policies */}
                        <div className="mt-8 bg-gray-50 rounded-lg p-6">
                            <h3 className="text-lg font-medium text-gray-900 mb-4">Library Policies</h3>
                            <ul className="space-y-2 text-sm text-gray-600">
                                <li className="flex items-start">
                                    <span className="flex-shrink-0 h-5 w-5 text-green-500 mr-2">•</span>
                                    <span>Standard loan period is 30 days.</span>
                                </li>
                                <li className="flex items-start">
                                    <span className="flex-shrink-0 h-5 w-5 text-green-500 mr-2">•</span>
                                    <span>You may renew books up to 2 times if no one else has requested them.</span>
                                </li>
                                <li className="flex items-start">
                                    <span className="flex-shrink-0 h-5 w-5 text-green-500 mr-2">•</span>
                                    <span>Late fees are $0.50 per day for overdue items.</span>
                                </li>
                                <li className="flex items-start">
                                    <span className="flex-shrink-0 h-5 w-5 text-green-500 mr-2">•</span>
                                    <span>Please return books in good condition to avoid damage fees.</span>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
