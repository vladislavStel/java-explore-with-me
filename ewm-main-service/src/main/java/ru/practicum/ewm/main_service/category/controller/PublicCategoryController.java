package ru.practicum.ewm.main_service.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.category.dto.CategoryDto;
import ru.practicum.ewm.main_service.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10", required = false) @Positive int size) {
        log.info("MainService: Get categories, from={}, size={}", from, size);
        return categoryService.findCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable("catId") long catId) {
        log.info("MainService: Get category by id, id={}", catId);
        return categoryService.findCategoryById(catId);
    }

}