'use client';

import { useState } from 'react';
import { FiSearch, FiFilter, FiX } from 'react-icons/fi';
import Button from '@/components/shared/Button';
import Input from '@/components/shared/Input';

interface SearchAndFilterProps {
  onSearch: (searchTerm: string) => void;
  onFilter: (filters: BookFilters) => void;
  onClear: () => void;
  searchTerm: string;
  categories?: string[];
}

export interface BookFilters {
  category?: string;
  status?: 'available' | 'borrowed' | 'maintenance' | '';
  author?: string;
  publishedAfter?: string;
  publishedBefore?: string;
}

export default function SearchAndFilter({ 
  onSearch, 
  onFilter, 
  onClear, 
  searchTerm,
  categories = []
}: SearchAndFilterProps) {
  const [showFilters, setShowFilters] = useState(false);
  const [filters, setFilters] = useState<BookFilters>({
    category: '',
    status: '',
    author: '',
    publishedAfter: '',
    publishedBefore: ''
  });

  const handleSearchChange = (value: string) => {
    onSearch(value);
  };

  const handleFilterChange = (key: keyof BookFilters, value: string) => {
    const newFilters = { ...filters, [key]: value };
    setFilters(newFilters);
    onFilter(newFilters);
  };

  const handleClearFilters = () => {
    const clearedFilters: BookFilters = {
      category: '',
      status: '',
      author: '',
      publishedAfter: '',
      publishedBefore: ''
    };
    setFilters(clearedFilters);
    onFilter(clearedFilters);
    onClear();
  };

  const hasActiveFilters = Object.values(filters).some(value => value !== '');

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 mb-6">
      {/* Search Bar */}
      <div className="flex gap-3 mb-4">
        <div className="flex-1 relative">
          <FiSearch className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <Input
            type="text"
            placeholder="Search books by title, author, ISBN..."
            value={searchTerm}
            onChange={(e) => handleSearchChange(e.target.value)}
            className="pl-10"
          />
        </div>
        <Button
          variant="secondary"
          onClick={() => setShowFilters(!showFilters)}
          className="flex items-center"
        >
          <FiFilter className="w-4 h-4 mr-2" />
          Filters
          {hasActiveFilters && (
            <span className="ml-2 bg-blue-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
              {Object.values(filters).filter(value => value !== '').length}
            </span>
          )}
        </Button>
        {(searchTerm || hasActiveFilters) && (
          <Button
            variant="secondary"
            onClick={handleClearFilters}
            className="flex items-center"
          >
            <FiX className="w-4 h-4 mr-2" />
            Clear
          </Button>
        )}
      </div>

      {/* Advanced Filters */}
      {showFilters && (
        <div className="border-t border-gray-200 pt-4">
          <h3 className="text-sm font-medium text-gray-700 mb-3">Advanced Filters</h3>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {/* Category Filter */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Category
              </label>
              <select
                value={filters.category}
                onChange={(e) => handleFilterChange('category', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">All Categories</option>
                {categories.map((category) => (
                  <option key={category} value={category}>
                    {category}
                  </option>
                ))}
              </select>
            </div>

            {/* Status Filter */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Status
              </label>
              <select
                value={filters.status || ''}
                onChange={(e) => handleFilterChange('status', e.target.value as '' | 'available' | 'borrowed' | 'maintenance')}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">All Status</option>
                <option value="available">Available</option>
                <option value="borrowed">Borrowed</option>
                <option value="maintenance">Under Maintenance</option>
              </select>
            </div>

            {/* Author Filter */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Author
              </label>
              <Input
                type="text"
                placeholder="Filter by author"
                value={filters.author}
                onChange={(e) => handleFilterChange('author', e.target.value)}
              />
            </div>

            {/* Published After */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Published After
              </label>
              <Input
                type="date"
                value={filters.publishedAfter}
                onChange={(e) => handleFilterChange('publishedAfter', e.target.value)}
              />
            </div>

            {/* Published Before */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Published Before
              </label>
              <Input
                type="date"
                value={filters.publishedBefore}
                onChange={(e) => handleFilterChange('publishedBefore', e.target.value)}
              />
            </div>
          </div>

          {/* Filter Actions */}
          <div className="flex justify-end gap-2 mt-4">
            <Button
              variant="secondary"
              size="sm"
              onClick={() => setShowFilters(false)}
            >
              Hide Filters
            </Button>
            <Button
              variant="secondary"
              size="sm"
              onClick={handleClearFilters}
            >
              Clear All Filters
            </Button>
          </div>
        </div>
      )}
    </div>
  );
} 