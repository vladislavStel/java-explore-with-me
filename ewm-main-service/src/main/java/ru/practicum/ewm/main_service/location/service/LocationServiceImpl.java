package ru.practicum.ewm.main_service.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main_service.location.model.Location;
import ru.practicum.ewm.main_service.location.repository.LocationRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }

    @Override
    public Location checkAvailabilityLocation(float lat, float lon) {
        Optional<Location> foundedLocation = locationRepository.findByLatAndLon(lat, lon);
        if (foundedLocation.isPresent()) {
            return foundedLocation.get();
        } else {
            Location location = Location.builder()
                    .lat(lat)
                    .lon(lon)
                    .build();
            return locationRepository.save(location);
        }
    }

}