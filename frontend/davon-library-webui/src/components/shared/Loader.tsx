'use client';

interface LoaderProps {
  size?: 'sm' | 'md' | 'lg';
  color?: 'primary' | 'white' | 'gray';
  fullScreen?: boolean;
}

export default function Loader({
  size = 'md',
  color = 'primary',
  fullScreen = false,
}: LoaderProps) {
  const sizes = {
    sm: 'h-4 w-4',
    md: 'h-8 w-8',
    lg: 'h-12 w-12',
  };

  const colors = {
    primary: 'border-indigo-500',
    white: 'border-white',
    gray: 'border-gray-500',
  };

  const spinner = (
    <div
      className={`
        animate-spin rounded-full border-2 border-t-transparent
        ${sizes[size]}
        ${colors[color]}
      `}
    />
  );

  if (fullScreen) {
    return (
      <div className="fixed inset-0 flex items-center justify-center bg-white bg-opacity-75 z-50">
        {spinner}
      </div>
    );
  }

  return spinner;
} 