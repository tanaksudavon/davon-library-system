export interface Book {
    id: string;
    title: string;
    author: string;
    isbn: string;
    category: string;
    description: string;
    publishDate: string;
    status: 'available' | 'borrowed' | 'maintenance';
    coverImage?: string;
    createdAt: string;
    updatedAt: string;
}

export interface BookCreateInput {
    title: string;
    author: string;
    isbn: string;
    category: string;
    description: string;
    publishDate: string;
    status: 'available' | 'borrowed' | 'maintenance';
    coverImage?: string;
}

export interface BookUpdateInput {
    title?: string;
    author?: string;
    isbn?: string;
    category?: string;
    description?: string;
    publishDate?: string;
    status?: 'available' | 'borrowed' | 'maintenance';
    coverImage?: string;
} 