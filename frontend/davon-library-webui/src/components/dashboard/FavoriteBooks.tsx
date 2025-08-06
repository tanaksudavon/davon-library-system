'use client';

import React, { useState, useEffect } from 'react';
import { FiHeart, FiBook, FiPlus } from 'react-icons/fi';
import { useRouter } from 'next/navigation';
import { authService } from '@/lib/services/auth-service';
import { favoriteService, type Favorite } from '@/lib/api/services/favorite.service';

export default function FavoriteBooks() {
  const router = useRouter();
  const [favorites, setFavorites] = useState<Favorite[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchFavorites = async () => {
      try {
        const currentUser = authService.getCurrentUser();
        if (!currentUser?.id) return;

        const userFavorites = await favoriteService.getUserFavorites(currentUser.id);
        setFavorites(userFavorites.slice(0, 5)); // Show top 5 favorites
      } catch (error) {
        console.error('Failed to fetch favorites:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchFavorites();
  }, []);

  const handleRemoveFavorite = async (bookId: number) => {
    try {
      const currentUser = authService.getCurrentUser();
      if (!currentUser?.id) return;

      await favoriteService.removeFavorite(currentUser.id, bookId);
      setFavorites(prev => prev.filter(fav => fav.book.id !== bookId));
    } catch (error) {
      console.error('Failed to remove favorite:', error);
    }
  };

  const handleBrowseBooks = () => {
    router.push('/dashboard/books');
  };

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-sm border">
        <div className="p-6 border-b border-gray-200">
          <h3 className="text-lg font-semibold text-gray-900">Favorite Books</h3>
        </div>
        <div className="p-6">
          <div className="space-y-3">
            {[...Array(3)].map((_, i) => (
              <div key={i} className="animate-pulse">
                <div className="flex items-center space-x-3">
                  <div className="w-8 h-8 bg-gray-200 rounded-full"></div>
                  <div className="flex-1">
                    <div className="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
                    <div className="h-3 bg-gray-200 rounded w-1/2"></div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border">
      <div className="p-6 border-b border-gray-200">
        <div className="flex items-center justify-between">
          <h3 className="text-lg font-semibold text-gray-900">Favorite Books</h3>
          <span className="bg-pink-100 text-pink-800 text-xs px-2 py-1 rounded-full">
            {favorites.length} {favorites.length === 1 ? 'book' : 'books'}
          </span>
        </div>
      </div>
      <div className="p-6">
        {favorites.length === 0 ? (
          <div className="text-center py-8">
            <FiHeart className="w-12 h-12 text-gray-400 mx-auto mb-3" />
            <p className="text-gray-500">No favorite books yet</p>
            <p className="text-sm text-gray-400 mt-1">
              Heart books you love to see them here
            </p>
            <button 
              onClick={handleBrowseBooks}
              className="mt-3 inline-flex items-center px-3 py-2 text-sm text-primary-600 hover:text-primary-800 font-medium hover:bg-primary-50 rounded-lg transition-colors"
            >
              <FiPlus className="w-4 h-4 mr-1" />
              Browse books
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            {favorites.map((favorite) => (
              <div key={favorite.id} className="flex items-start space-x-3 group">
                <div className="flex-shrink-0 mt-1">
                  <div className="w-8 h-8 bg-pink-100 text-pink-600 rounded-full flex items-center justify-center">
                    <FiHeart className="w-4 h-4 fill-current" />
                  </div>
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900 truncate">
                    {favorite.book.title}
                  </p>
                  <p className="text-sm text-gray-600">
                    by {favorite.book.author.firstName} {favorite.book.author.lastName}
                  </p>
                  <div className="flex items-center justify-between mt-2">
                    <span className="text-xs text-gray-500">
                      {favorite.book.category.name}
                    </span>
                    <button
                      onClick={() => handleRemoveFavorite(favorite.book.id)}
                      className="opacity-0 group-hover:opacity-100 text-xs text-red-500 hover:text-red-700 font-medium transition-opacity"
                    >
                      Remove
                    </button>
                  </div>
                </div>
              </div>
            ))}
            
            {favorites.length > 0 && (
              <div className="mt-4 pt-4 border-t border-gray-200">
                <button 
                  onClick={handleBrowseBooks}
                  className="text-sm text-primary-600 hover:text-primary-800 font-medium hover:bg-primary-50 px-2 py-1 rounded transition-colors"
                >
                  View all favorites
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}