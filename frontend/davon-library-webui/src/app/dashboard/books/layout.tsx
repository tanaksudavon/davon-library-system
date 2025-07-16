import ProtectedRoute from '@/components/auth/ProtectedRoute';

export default function BooksLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <ProtectedRoute requiredRole="admin">
      {children}
    </ProtectedRoute>
  );
} 