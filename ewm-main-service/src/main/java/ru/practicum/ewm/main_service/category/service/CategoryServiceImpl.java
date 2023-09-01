package ru.practicum.ewm.main_service.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main_service.category.dto.CategoryDto;
import ru.practicum.ewm.main_service.category.dto.NewCategoryDto;
import ru.practicum.ewm.main_service.category.mapper.CategoryMapper;
import ru.practicum.ewm.main_service.category.model.Category;
import ru.practicum.ewm.main_service.category.repository.CategoryRepository;
import ru.practicum.ewm.main_service.event.repository.EventRepository;
import ru.practicum.ewm.main_service.exception.error.ObjectAlreadyExistException;
import ru.practicum.ewm.main_service.exception.error.ObjectNotFoundException;
import ru.practicum.ewm.main_service.exception.error.ValidationException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto saveCategory(NewCategoryDto newCategoryDto) {
        try {
            log.info("Save category in the repository, category={}", newCategoryDto);
            return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(newCategoryDto)));
        } catch (DataIntegrityViolationException e) {
            log.error("Category with name is already registered, name={}", newCategoryDto.getName());
            throw new ObjectAlreadyExistException("Category with name " +
                    newCategoryDto.getName() + " is already registered.");
        }
    }

    @Override
    public void deleteCategoryById(long catId) {
        validateCategoryById(catId);
        if (eventRepository.countEventByCategoryId(catId) > 0) {
            throw new ValidationException(String.format("The category with ID=%d is not empty", catId));
        }
        categoryRepository.deleteById(catId);
        log.info("Category deleted by id={}", catId);
    }

    @Override
    public CategoryDto updateCategory(long catId, CategoryDto categoryDto) {
        Category category = getById(catId);
        Category validCategory = categoryRepository.findByName(categoryDto.getName()).orElse(null);
        if (validCategory != null && !Objects.equals(validCategory.getId(), catId)) {
            log.error("Category with name is already registered, name={}", categoryDto.getName());
            throw new ObjectAlreadyExistException("Category with name " +
                    categoryDto.getName() + " is already registered.");
        }
        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
        }
        log.info("Update category in the repository, category={}", categoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> findCategories(int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        List<Category> category = categoryRepository.findAll(page).toList();
        log.info("Got a list of categories in the repository, from={}, size={}", from, size);
        return category.stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto findCategoryById(long catId) {
        Category category = getById(catId);
        log.info("Got a category in the repository, id={}", catId);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void validateCategoryById(long catId) {
        if (!categoryRepository.existsById(catId)) {
            log.error("Category not found, id={}", catId);
            throw new ObjectNotFoundException(String.format("Category not found: id=%d", catId));
        }
    }

    @Override
    public Category getById(long catId) {
        log.error("Category not found, id={}", catId);
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Category not found: id=%d", catId)));
    }

}