import { httpClient } from '../client';
import { API_CONFIG } from '../config';
import { Category } from '../types';

export interface CategoryCreateRequest {
  name: string;
  description?: string;
}

export interface CategoryUpdateRequest {
  name?: string;
  description?: string;
}

export class CategoryService {
  /**
   * Get all categories
   */
  async getAllCategories(): Promise<Category[]> {
    try {
      return await httpClient.get<Category[]>(API_CONFIG.ENDPOINTS.CATEGORIES.BASE);
    } catch (error) {
      console.error('Failed to fetch categories:', error);
      throw error;
    }
  }

  /**
   * Get category by ID
   */
  async getCategoryById(id: number): Promise<Category> {
    try {
      return await httpClient.get<Category>(API_CONFIG.ENDPOINTS.CATEGORIES.BY_ID(id));
    } catch (error) {
      console.error(`Failed to fetch category with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Create new category
   */
  async createCategory(categoryData: CategoryCreateRequest): Promise<Category> {
    try {
      return await httpClient.post<Category>(
        API_CONFIG.ENDPOINTS.CATEGORIES.BASE,
        categoryData
      );
    } catch (error) {
      console.error('Failed to create category:', error);
      throw error;
    }
  }

  /**
   * Update existing category
   */
  async updateCategory(id: number, categoryData: CategoryUpdateRequest): Promise<Category> {
    try {
      return await httpClient.put<Category>(
        API_CONFIG.ENDPOINTS.CATEGORIES.BY_ID(id),
        categoryData
      );
    } catch (error) {
      console.error(`Failed to update category with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Delete category
   */
  async deleteCategory(id: number): Promise<void> {
    try {
      await httpClient.delete<void>(API_CONFIG.ENDPOINTS.CATEGORIES.BY_ID(id));
    } catch (error) {
      console.error(`Failed to delete category with ID ${id}:`, error);
      throw error;
    }
  }

  /**
   * Get categories for dropdown/select components
   */
  async getCategoriesForDropdown(): Promise<{ id: number; label: string; value: string }[]> {
    try {
      const categories = await this.getAllCategories();
      return categories.map(category => ({
        id: category.id,
        label: category.name,
        value: category.name
      }));
    } catch (error) {
      console.error('Failed to fetch categories for dropdown:', error);
      return [];
    }
  }

  /**
   * Check if category name is available
   */
  async isCategoryNameAvailable(name: string, excludeId?: number): Promise<boolean> {
    try {
      const categories = await this.getAllCategories();
      return !categories.some(cat => 
        cat.name.toLowerCase() === name.toLowerCase() && 
        cat.id !== excludeId
      );
    } catch (error) {
      console.error('Failed to check category name availability:', error);
      return false;
    }
  }

  /**
   * Get categories with book count (requires additional API call)
   */
  async getCategoriesWithBookCount(): Promise<(Category & { bookCount: number })[]> {
    try {
      // This would require a backend endpoint that includes book counts
      // For now, we'll return categories with bookCount as 0
      const categories = await this.getAllCategories();
      return categories.map(category => ({
        ...category,
        bookCount: 0 // TODO: Implement backend endpoint for book counts
      }));
    } catch (error) {
      console.error('Failed to fetch categories with book count:', error);
      throw error;
    }
  }
}

// Create and export singleton instance
export const categoryService = new CategoryService(); 