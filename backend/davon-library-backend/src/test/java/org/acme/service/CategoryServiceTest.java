package org.acme.service;

import org.acme.model.Category;
import org.acme.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Debug/Test class for CategoryService
 * Used for debugging category management functionality
 */
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory1;
    private Category testCategory2;
    private Category testCategory3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Debug: Setup test data
        System.out.println("DEBUG: Setting up CategoryServiceTest");

        testCategory1 = new Category();
        testCategory1.setId(1L);
        testCategory1.setName("Fiction");
        testCategory1.setDescription("Literary works of imaginative narration");
        testCategory1.setCreatedAt(LocalDateTime.now().minusDays(10));
        testCategory1.setUpdatedAt(LocalDateTime.now().minusDays(5));

        testCategory2 = new Category();
        testCategory2.setId(2L);
        testCategory2.setName("Science Fiction");
        testCategory2.setDescription("Futuristic and speculative fiction");
        testCategory2.setCreatedAt(LocalDateTime.now().minusDays(8));
        testCategory2.setUpdatedAt(LocalDateTime.now().minusDays(3));

        testCategory3 = new Category();
        testCategory3.setId(3L);
        testCategory3.setName("Mystery");
        testCategory3.setDescription("Detective and mystery novels");
        testCategory3.setCreatedAt(LocalDateTime.now().minusDays(15));
        testCategory3.setUpdatedAt(LocalDateTime.now().minusDays(1));

        System.out.println("DEBUG: Test categories initialized - " + testCategory1.getName() +
                ", " + testCategory2.getName() + ", " + testCategory3.getName());
    }

    @Test
    @DisplayName("Should get all categories successfully")
    void testGetAllCategories_Success() {
        System.out.println("DEBUG: Testing getAllCategories - Success scenario");

        // Given
        List<Category> expectedCategories = Arrays.asList(testCategory1, testCategory2, testCategory3);
        when(categoryRepository.listAll()).thenReturn(expectedCategories);

        // When
        List<Category> result = categoryService.getAllCategories();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(expectedCategories, result);

        verify(categoryRepository, times(1)).listAll();
        System.out.println("DEBUG: getAllCategories returned " + result.size() + " categories");
    }

    @Test
    @DisplayName("Should get all categories when repository is empty")
    void testGetAllCategories_EmptyRepository() {
        System.out.println("DEBUG: Testing getAllCategories - Empty repository scenario");

        // Given
        when(categoryRepository.listAll()).thenReturn(Collections.emptyList());

        // When
        List<Category> result = categoryService.getAllCategories();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());

        verify(categoryRepository, times(1)).listAll();
        System.out.println("DEBUG: getAllCategories returned empty list as expected");
    }

    @Test
    @DisplayName("Should get category by ID successfully")
    void testGetCategoryById_Success() {
        System.out.println("DEBUG: Testing getCategoryById - Success scenario");

        // Given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(testCategory1);

        // When
        Category result = categoryService.getCategoryById(categoryId);

        // Then
        assertNotNull(result);
        assertEquals(testCategory1.getId(), result.getId());
        assertEquals(testCategory1.getName(), result.getName());
        assertEquals(testCategory1.getDescription(), result.getDescription());

        verify(categoryRepository, times(1)).findById(categoryId);
        System.out.println("DEBUG: getCategoryById returned category: " + result.getName());
    }

    @Test
    @DisplayName("Should return null when category not found by ID")
    void testGetCategoryById_NotFound() {
        System.out.println("DEBUG: Testing getCategoryById - Not found scenario");

        // Given
        Long nonExistentId = 999L;
        when(categoryRepository.findById(nonExistentId)).thenReturn(null);

        // When
        Category result = categoryService.getCategoryById(nonExistentId);

        // Then
        assertNull(result);

        verify(categoryRepository, times(1)).findById(nonExistentId);
        System.out.println("DEBUG: getCategoryById returned null for non-existent ID: " + nonExistentId);
    }

    @Test
    @DisplayName("Should create new category successfully")
    void testCreateCategory_Success() {
        System.out.println("DEBUG: Testing createCategory - Success scenario");

        // Given
        Category newCategory = new Category();
        newCategory.setName("Horror");
        newCategory.setDescription("Scary and suspenseful stories");

        // When
        Category result = categoryService.createCategory(newCategory);

        // Then
        assertNotNull(result);
        assertEquals("Horror", result.getName());
        assertEquals("Scary and suspenseful stories", result.getDescription());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(categoryRepository, times(1)).persist(newCategory);
        System.out.println("DEBUG: createCategory successful for: " + result.getName());
    }

    @Test
    @DisplayName("Should create category with minimal required fields")
    void testCreateCategory_MinimalFields() {
        System.out.println("DEBUG: Testing createCategory - Minimal fields scenario");

        // Given
        Category minimalCategory = new Category();
        minimalCategory.setName("Romance");

        // When
        Category result = categoryService.createCategory(minimalCategory);

        // Then
        assertNotNull(result);
        assertEquals("Romance", result.getName());
        assertNull(result.getDescription());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(categoryRepository, times(1)).persist(minimalCategory);
        System.out.println("DEBUG: createCategory with minimal fields successful");
    }

    @Test
    @DisplayName("Should create category with null description")
    void testCreateCategory_NullDescription() {
        System.out.println("DEBUG: Testing createCategory - Null description scenario");

        // Given
        Category categoryWithNullDesc = new Category();
        categoryWithNullDesc.setName("Biography");
        categoryWithNullDesc.setDescription(null);

        // When
        Category result = categoryService.createCategory(categoryWithNullDesc);

        // Then
        assertNotNull(result);
        assertEquals("Biography", result.getName());
        assertNull(result.getDescription());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(categoryRepository, times(1)).persist(categoryWithNullDesc);
        System.out.println("DEBUG: createCategory with null description successful");
    }

    @Test
    @DisplayName("Should update existing category successfully")
    void testUpdateCategory_Success() {
        System.out.println("DEBUG: Testing updateCategory - Success scenario");

        // Given
        Long categoryId = 1L;
        Category updatedData = new Category();
        updatedData.setName("Updated Fiction");
        updatedData.setDescription("Updated description for fiction books");

        when(categoryRepository.findById(categoryId)).thenReturn(testCategory1);

        // When
        Category result = categoryService.updateCategory(categoryId, updatedData);

        // Then
        assertNotNull(result);
        assertEquals("Updated Fiction", result.getName());
        assertEquals("Updated description for fiction books", result.getDescription());
        assertNotNull(result.getUpdatedAt());

        verify(categoryRepository, times(1)).findById(categoryId);
        System.out.println("DEBUG: updateCategory successful - updated to: " + result.getName());
    }

    @Test
    @DisplayName("Should return null when updating non-existent category")
    void testUpdateCategory_NotFound() {
        System.out.println("DEBUG: Testing updateCategory - Not found scenario");

        // Given
        Long nonExistentId = 999L;
        Category updatedData = new Category();
        updatedData.setName("Updated Name");
        updatedData.setDescription("Updated Description");

        when(categoryRepository.findById(nonExistentId)).thenReturn(null);

        // When
        Category result = categoryService.updateCategory(nonExistentId, updatedData);

        // Then
        assertNull(result);

        verify(categoryRepository, times(1)).findById(nonExistentId);
        System.out.println("DEBUG: updateCategory returned null for non-existent ID: " + nonExistentId);
    }

    @Test
    @DisplayName("Should update category with partial data")
    void testUpdateCategory_PartialUpdate() {
        System.out.println("DEBUG: Testing updateCategory - Partial update scenario");

        // Given
        Long categoryId = 2L;
        Category partialUpdate = new Category();
        partialUpdate.setName("Sci-Fi"); // Only updating name
        partialUpdate.setDescription(null); // Setting description to null

        when(categoryRepository.findById(categoryId)).thenReturn(testCategory2);

        // When
        Category result = categoryService.updateCategory(categoryId, partialUpdate);

        // Then
        assertNotNull(result);
        assertEquals("Sci-Fi", result.getName());
        assertNull(result.getDescription()); // Should be null as per update
        assertNotNull(result.getUpdatedAt());

        verify(categoryRepository, times(1)).findById(categoryId);
        System.out.println("DEBUG: updateCategory partial update successful - name: " + result.getName());
    }

    @Test
    @DisplayName("Should delete existing category successfully")
    void testDeleteCategory_Success() {
        System.out.println("DEBUG: Testing deleteCategory - Success scenario");

        // Given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(testCategory1);

        // When
        boolean result = categoryService.deleteCategory(categoryId);

        // Then
        assertTrue(result);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).delete(testCategory1);
        System.out.println("DEBUG: deleteCategory successful for ID: " + categoryId);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent category")
    void testDeleteCategory_NotFound() {
        System.out.println("DEBUG: Testing deleteCategory - Not found scenario");

        // Given
        Long nonExistentId = 999L;
        when(categoryRepository.findById(nonExistentId)).thenReturn(null);

        // When
        boolean result = categoryService.deleteCategory(nonExistentId);

        // Then
        assertFalse(result);

        verify(categoryRepository, times(1)).findById(nonExistentId);
        verify(categoryRepository, never()).delete(any());
        System.out.println("DEBUG: deleteCategory returned false for non-existent ID: " + nonExistentId);
    }

    @Test
    @DisplayName("Should verify timestamp updates on category creation")
    void testCreateCategory_TimestampVerification() {
        System.out.println("DEBUG: Testing createCategory - Timestamp verification");

        // Given
        Category newCategory = new Category();
        newCategory.setName("Timestamp Test");
        newCategory.setDescription("Testing timestamp functionality");

        LocalDateTime beforeCreation = LocalDateTime.now();

        // When
        Category result = categoryService.createCategory(newCategory);

        LocalDateTime afterCreation = LocalDateTime.now();

        // Then
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getCreatedAt().isAfter(beforeCreation.minusSeconds(1)));
        assertTrue(result.getCreatedAt().isBefore(afterCreation.plusSeconds(1)));
        assertTrue(result.getUpdatedAt().isAfter(beforeCreation.minusSeconds(1)));
        assertTrue(result.getUpdatedAt().isBefore(afterCreation.plusSeconds(1)));

        System.out.println("DEBUG: Timestamp verification successful - created: " + result.getCreatedAt() +
                ", updated: " + result.getUpdatedAt());
    }

    @Test
    @DisplayName("Should verify timestamp updates on category update")
    void testUpdateCategory_TimestampVerification() {
        System.out.println("DEBUG: Testing updateCategory - Timestamp verification");

        // Given
        Long categoryId = 3L;
        Category updatedData = new Category();
        updatedData.setName("Updated Mystery");
        updatedData.setDescription("Updated mystery description");

        LocalDateTime originalUpdatedAt = testCategory3.getUpdatedAt();
        when(categoryRepository.findById(categoryId)).thenReturn(testCategory3);

        LocalDateTime beforeUpdate = LocalDateTime.now();

        // When
        Category result = categoryService.updateCategory(categoryId, updatedData);

        LocalDateTime afterUpdate = LocalDateTime.now();

        // Then
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getUpdatedAt().isAfter(beforeUpdate.minusSeconds(1)));
        assertTrue(result.getUpdatedAt().isBefore(afterUpdate.plusSeconds(1)));
        assertTrue(result.getUpdatedAt().isAfter(originalUpdatedAt));

        System.out.println("DEBUG: Update timestamp verification successful - original: " + originalUpdatedAt +
                ", updated: " + result.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle category with empty name")
    void testCreateCategory_EmptyName() {
        System.out.println("DEBUG: Testing createCategory - Empty name scenario");

        // Given
        Category emptyNameCategory = new Category();
        emptyNameCategory.setName("");
        emptyNameCategory.setDescription("Category with empty name");

        // When
        Category result = categoryService.createCategory(emptyNameCategory);

        // Then
        assertNotNull(result);
        assertEquals("", result.getName());
        assertEquals("Category with empty name", result.getDescription());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(categoryRepository, times(1)).persist(emptyNameCategory);
        System.out.println("DEBUG: createCategory with empty name handled successfully");
    }

    @Test
    @DisplayName("Should handle category with very long description")
    void testCreateCategory_LongDescription() {
        System.out.println("DEBUG: Testing createCategory - Long description scenario");

        // Given
        String longDescription = "This is a very long description that contains many words and characters to test how the system handles lengthy text input for category descriptions. "
                .repeat(5);
        Category longDescCategory = new Category();
        longDescCategory.setName("Long Desc Category");
        longDescCategory.setDescription(longDescription);

        // When
        Category result = categoryService.createCategory(longDescCategory);

        // Then
        assertNotNull(result);
        assertEquals("Long Desc Category", result.getName());
        assertEquals(longDescription, result.getDescription());
        assertTrue(result.getDescription().length() > 500);

        verify(categoryRepository, times(1)).persist(longDescCategory);
        System.out.println(
                "DEBUG: createCategory with long description successful - length: " + result.getDescription().length());
    }

    @Test
    @DisplayName("Should verify category state after multiple operations")
    void testCategory_MultipleOperations() {
        System.out.println("DEBUG: Testing category - Multiple operations scenario");

        // Given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(testCategory1);

        // When - First get the category
        Category retrieved = categoryService.getCategoryById(categoryId);

        // Then - Verify first operation
        assertNotNull(retrieved);
        assertEquals("Fiction", retrieved.getName());

        // When - Update the category
        Category updateData = new Category();
        updateData.setName("Updated Fiction");
        updateData.setDescription("Updated description");
        Category updated = categoryService.updateCategory(categoryId, updateData);

        // Then - Verify update operation
        assertNotNull(updated);
        assertEquals("Updated Fiction", updated.getName());
        assertEquals("Updated description", updated.getDescription());

        // Verify all interactions
        verify(categoryRepository, times(2)).findById(categoryId); // Called twice

        System.out.println("DEBUG: Multiple operations test completed successfully");
    }
}