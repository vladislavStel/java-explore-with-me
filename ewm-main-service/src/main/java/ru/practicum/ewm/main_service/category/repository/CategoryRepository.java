package ru.practicum.ewm.main_service.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main_service.category.model.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

}