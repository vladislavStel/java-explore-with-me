package ru.practicum.ewm.main_service.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main_service.location.model.Location;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLatAndLon(float lat, float lon);

}