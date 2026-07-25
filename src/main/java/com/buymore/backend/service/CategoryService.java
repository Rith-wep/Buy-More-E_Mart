package com.buymore.backend.service;

import com.buymore.backend.dto.CategoryRequest;
import com.buymore.backend.dto.CategoryResponse;
import com.buymore.backend.entity.Category;
import com.buymore.backend.exception.ConflictException;
import com.buymore.backend.exception.ResourceNotFoundException;
import com.buymore.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new ConflictException("Category already exists: " + request.name());
        }

        Category category = Category.builder().name(request.name()).build();
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findOrThrow(id);
        category.setName(request.name());
        return toResponse(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = findOrThrow(id);

        if (!category.getProducts().isEmpty()) {
            throw new ConflictException("Cannot delete a category that still has products");
        }

        categoryRepository.delete(category);
    }

    private Category findOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
