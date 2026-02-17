package com.connect.pairr.service;

import com.connect.pairr.model.dto.CategoryResponse;
import com.connect.pairr.model.entity.Category;
import com.connect.pairr.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryService::toCategoryResponse)
                .toList();
    }

    private static CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
