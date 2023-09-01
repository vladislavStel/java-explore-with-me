package ru.practicum.ewm.main_service.category.service;

import ru.practicum.ewm.main_service.category.dto.CategoryDto;
import ru.practicum.ewm.main_service.category.dto.NewCategoryDto;
import ru.practicum.ewm.main_service.category.model.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto saveCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(long catId);

    CategoryDto updateCategory(long catId, CategoryDto categoryDto);

    List<CategoryDto> findCategories(int from, int size);

    CategoryDto findCategoryById(long catId);

    void validateCategoryById(long catId);

    Category getById(long catId);

}