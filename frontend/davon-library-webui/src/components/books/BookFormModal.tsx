import { useState, useEffect } from 'react';
import { Book, BookStatus, Author, Category, BookCreateRequest, BookUpdateRequest } from '@/lib/api/types';
import { bookService } from '@/lib/api/services/book.service';
import { authorService } from '@/lib/api/services/author.service';
import { categoryService } from '@/lib/api/services/category.service';
import { useLibrary } from '@/contexts/LibraryContext';
import { FiX, FiPlus, FiSearch } from 'react-icons/fi';

interface BookFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: (book: Book) => void;
  book?: Book;
}

export default function BookFormModal({ isOpen, onClose, onSuccess, book }: BookFormModalProps) {
  const { actions } = useLibrary();
  
  const [formData, setFormData] = useState<BookCreateRequest>({
    title: '',
    authorId: 0,
    isbn: '',
    categoryId: 0,
    description: '',
    publishDate: '',
    status: BookStatus.AVAILABLE,
  });

  const [authors, setAuthors] = useState<Author[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(true);
  const [errors, setErrors] = useState<Record<string, string>>({});

  // New author creation
  const [showNewAuthor, setShowNewAuthor] = useState(false);
  const [newAuthor, setNewAuthor] = useState({
    firstName: '',
    lastName: '',
    biography: '',
    birthDate: '',
    nationality: ''
  });

  // New category creation
  const [showNewCategory, setShowNewCategory] = useState(false);
  const [newCategory, setNewCategory] = useState({
    name: '',
    description: ''
  });

  useEffect(() => {
    if (isOpen) {
      loadInitialData();
    }
  }, [isOpen]);

  useEffect(() => {
    if (book) {
      setFormData({
        title: book.title,
        authorId: book.author.id,
        isbn: book.isbn || '',
        categoryId: book.category.id,
        description: book.description || '',
        publishDate: book.publishDate || '',
        status: book.status,
      });
    } else {
      // Reset form for new book
      setFormData({
        title: '',
        authorId: 0,
        isbn: '',
        categoryId: 0,
        description: '',
        publishDate: '',
        status: BookStatus.AVAILABLE,
      });
    }
  }, [book]);

  const loadInitialData = async () => {
    setLoadingData(true);
    try {
      const [authorsData, categoriesData] = await Promise.all([
        authorService.getAllAuthors(),
        categoryService.getAllCategories()
      ]);
      setAuthors(authorsData);
      setCategories(categoriesData);
      
      // Set default selections if not editing
      if (!book && authorsData.length > 0 && categoriesData.length > 0) {
        setFormData(prev => ({
          ...prev,
          authorId: authorsData[0].id,
          categoryId: categoriesData[0].id
        }));
      }
    } catch (error) {
      console.error('Failed to load authors and categories:', error);
    } finally {
      setLoadingData(false);
    }
  };

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.title.trim()) newErrors.title = 'Title is required';
    if (!formData.authorId) newErrors.authorId = 'Author is required';
    if (!formData.isbn.trim()) newErrors.isbn = 'ISBN is required';
    if (!formData.categoryId) newErrors.categoryId = 'Category is required';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;

    setLoading(true);
    try {
      let savedBook: Book;
      if (book) {
        const updateData: BookUpdateRequest = { id: book.id, ...formData };
        savedBook = await actions.updateBook(book.id, updateData);
      } else {
        savedBook = await actions.addBook(formData);
      }
      onSuccess(savedBook);
      onClose();
    } catch (error) {
      console.error('Failed to save book:', error);
      setErrors({ submit: 'Failed to save book. Please try again.' });
    } finally {
      setLoading(false);
    }
  };

  const handleCreateAuthor = async () => {
    if (!newAuthor.firstName.trim() || !newAuthor.lastName.trim()) {
      return;
    }

    try {
      const createdAuthor = await authorService.createAuthor(newAuthor);
      setAuthors([...authors, createdAuthor]);
      setFormData(prev => ({ ...prev, authorId: createdAuthor.id }));
      setNewAuthor({ firstName: '', lastName: '', biography: '', birthDate: '', nationality: '' });
      setShowNewAuthor(false);
    } catch (error) {
      console.error('Failed to create author:', error);
    }
  };

  const handleCreateCategory = async () => {
    if (!newCategory.name.trim()) {
      return;
    }

    try {
      const createdCategory = await categoryService.createCategory(newCategory);
      setCategories([...categories, createdCategory]);
      setFormData(prev => ({ ...prev, categoryId: createdCategory.id }));
      setNewCategory({ name: '', description: '' });
      setShowNewCategory(false);
    } catch (error) {
      console.error('Failed to create category:', error);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between p-6 border-b">
          <h2 className="text-xl font-semibold">
            {book ? 'Edit Book' : 'Add New Book'}
          </h2>
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-100 rounded-full"
          >
            <FiX className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-6">
          {errors.submit && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              {errors.submit}
            </div>
          )}

          {loadingData ? (
            <div className="flex items-center justify-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-primary-500"></div>
            </div>
          ) : (
            <>
              {/* Title */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Title *
                </label>
                <input
                  type="text"
                  value={formData.title}
                  onChange={(e) => setFormData(prev => ({ ...prev, title: e.target.value }))}
                  className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                    errors.title ? 'border-red-500' : 'border-gray-300'
                  }`}
                  placeholder="Enter book title"
                />
                {errors.title && <p className="text-red-500 text-sm mt-1">{errors.title}</p>}
              </div>

              {/* Author Selection */}
              <div>
                <div className="flex items-center justify-between mb-2">
                  <label className="block text-sm font-medium text-gray-700">
                    Author *
                  </label>
                  <button
                    type="button"
                    onClick={() => setShowNewAuthor(!showNewAuthor)}
                    className="text-primary-600 hover:text-primary-700 text-sm flex items-center"
                  >
                    <FiPlus className="w-4 h-4 mr-1" />
                    Add New Author
                  </button>
                </div>
                
                {showNewAuthor && (
                  <div className="bg-gray-50 p-4 rounded-lg mb-4 space-y-3">
                    <h4 className="font-medium text-gray-900">Create New Author</h4>
                    <div className="grid grid-cols-2 gap-3">
                      <input
                        type="text"
                        placeholder="First Name"
                        value={newAuthor.firstName}
                        onChange={(e) => setNewAuthor(prev => ({ ...prev, firstName: e.target.value }))}
                        className="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                      />
                      <input
                        type="text"
                        placeholder="Last Name"
                        value={newAuthor.lastName}
                        onChange={(e) => setNewAuthor(prev => ({ ...prev, lastName: e.target.value }))}
                        className="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                      />
                    </div>
                    <input
                      type="text"
                      placeholder="Nationality (optional)"
                      value={newAuthor.nationality}
                      onChange={(e) => setNewAuthor(prev => ({ ...prev, nationality: e.target.value }))}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                    />
                    <div className="flex space-x-2">
                      <button
                        type="button"
                        onClick={handleCreateAuthor}
                        className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700"
                      >
                        Create Author
                      </button>
                      <button
                        type="button"
                        onClick={() => setShowNewAuthor(false)}
                        className="bg-gray-300 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-400"
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                )}

                <select
                  value={formData.authorId}
                  onChange={(e) => setFormData(prev => ({ ...prev, authorId: parseInt(e.target.value) }))}
                  className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                    errors.authorId ? 'border-red-500' : 'border-gray-300'
                  }`}
                >
                  <option value={0}>Select an author</option>
                  {authors.map(author => (
                    <option key={author.id} value={author.id}>
                      {author.firstName} {author.lastName}
                    </option>
                  ))}
                </select>
                {errors.authorId && <p className="text-red-500 text-sm mt-1">{errors.authorId}</p>}
              </div>

              {/* Category Selection */}
              <div>
                <div className="flex items-center justify-between mb-2">
                  <label className="block text-sm font-medium text-gray-700">
                    Category *
                  </label>
                  <button
                    type="button"
                    onClick={() => setShowNewCategory(!showNewCategory)}
                    className="text-primary-600 hover:text-primary-700 text-sm flex items-center"
                  >
                    <FiPlus className="w-4 h-4 mr-1" />
                    Add New Category
                  </button>
                </div>

                {showNewCategory && (
                  <div className="bg-gray-50 p-4 rounded-lg mb-4 space-y-3">
                    <h4 className="font-medium text-gray-900">Create New Category</h4>
                    <input
                      type="text"
                      placeholder="Category Name"
                      value={newCategory.name}
                      onChange={(e) => setNewCategory(prev => ({ ...prev, name: e.target.value }))}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                    />
                    <textarea
                      placeholder="Description (optional)"
                      value={newCategory.description}
                      onChange={(e) => setNewCategory(prev => ({ ...prev, description: e.target.value }))}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                      rows={2}
                    />
                    <div className="flex space-x-2">
                      <button
                        type="button"
                        onClick={handleCreateCategory}
                        className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700"
                      >
                        Create Category
                      </button>
                      <button
                        type="button"
                        onClick={() => setShowNewCategory(false)}
                        className="bg-gray-300 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-400"
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                )}

                <select
                  value={formData.categoryId}
                  onChange={(e) => setFormData(prev => ({ ...prev, categoryId: parseInt(e.target.value) }))}
                  className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                    errors.categoryId ? 'border-red-500' : 'border-gray-300'
                  }`}
                >
                  <option value={0}>Select a category</option>
                  {categories.map(category => (
                    <option key={category.id} value={category.id}>
                      {category.name}
                    </option>
                  ))}
                </select>
                {errors.categoryId && <p className="text-red-500 text-sm mt-1">{errors.categoryId}</p>}
              </div>

              {/* ISBN */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  ISBN *
                </label>
                <input
                  type="text"
                  value={formData.isbn}
                  onChange={(e) => setFormData(prev => ({ ...prev, isbn: e.target.value }))}
                  className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                    errors.isbn ? 'border-red-500' : 'border-gray-300'
                  }`}
                  placeholder="Enter ISBN"
                />
                {errors.isbn && <p className="text-red-500 text-sm mt-1">{errors.isbn}</p>}
              </div>

              {/* Publish Date */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Publish Date
                </label>
                <input
                  type="date"
                  value={formData.publishDate}
                  onChange={(e) => setFormData(prev => ({ ...prev, publishDate: e.target.value }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
              </div>

              {/* Description */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Description
                </label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  rows={4}
                  placeholder="Enter book description"
                />
              </div>

              {/* Status */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Status
                </label>
                <select
                  value={formData.status}
                  onChange={(e) => setFormData(prev => ({ ...prev, status: e.target.value as BookStatus }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                >
                  <option value={BookStatus.AVAILABLE}>Available</option>
                  <option value={BookStatus.BORROWED}>Borrowed</option>
                  <option value={BookStatus.RESERVED}>Reserved</option>
                  <option value={BookStatus.MAINTENANCE}>Maintenance</option>
                </select>
              </div>
            </>
          )}

          {/* Form Actions */}
          <div className="flex justify-end space-x-4 pt-6 border-t">
            <button
              type="button"
              onClick={onClose}
              className="px-6 py-2 text-gray-700 bg-gray-200 rounded-lg hover:bg-gray-300"
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading || loadingData}
              className="px-6 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? (
                <>
                  <div className="animate-spin rounded-full h-4 w-4 border-t-2 border-white inline-block mr-2"></div>
                  {book ? 'Updating...' : 'Creating...'}
                </>
              ) : (
                book ? 'Update Book' : 'Create Book'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
} 