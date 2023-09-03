package ru.practicum.ewm.main_service.location.service;

import ru.practicum.ewm.main_service.location.model.Location;

public interface LocationService {

    Location saveLocation(Location location);

    Location checkAvailabilityLocation(float lat, float lon);

}