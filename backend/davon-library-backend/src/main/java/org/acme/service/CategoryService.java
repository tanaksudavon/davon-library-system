package org.acme.service;

import org.acme.model.Category;
import org.acme.repository.CategoryRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class CategoryService {

    @Inject
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.listAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public Category createCategory(Category category) {
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.persist(category);
        return category;
    }

    @Transactional
    public Category updateCategory(Long id, Category updatedCategory) {
        Category existing = categoryRepository.findById(id);
        if (existing != null) {
            existing.setName(updatedCategory.getName());
            existing.setDescription(updatedCategory.getDescription());
            existing.setUpdatedAt(LocalDateTime.now());
            return existing;
        }
        return null;
    }

    @Transactional
    public boolean deleteCategory(Long id) {
        Category category = categoryRepository.findById(id);
        if (category != null) {
            categoryRepository.delete(category);
            return true;
        }
        return false;
    }
}