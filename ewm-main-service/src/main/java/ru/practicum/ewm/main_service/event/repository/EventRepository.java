package ru.practicum.ewm.main_service.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main_service.event.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Long countEventByCategoryId(Long catId);

}