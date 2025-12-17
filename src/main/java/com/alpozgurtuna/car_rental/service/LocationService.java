package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.Location;
import com.alpozgurtuna.car_rental.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {

    private final LocationRepository locationRepository;

    public Location createLocation(Location location) {
        if (locationRepository.existsByCode(location.getCode())) {
            throw new IllegalArgumentException("Location with code " + location.getCode() + " already exists");
        }
        return locationRepository.save(location);
    }

    @Transactional(readOnly = true)
    public Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Location getLocationByCode(String code) {
        return locationRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with code: " + code));
    }

    @Transactional(readOnly = true)
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location updateLocation(Long id, Location locationDetails) {
        Location location = getLocationById(id);

        if (!location.getCode().equals(locationDetails.getCode()) &&
                locationRepository.existsByCode(locationDetails.getCode())) {
            throw new IllegalArgumentException("Location with code " + locationDetails.getCode() + " already exists");
        }

        location.setCode(locationDetails.getCode());
        location.setName(locationDetails.getName());

        return locationRepository.save(location);
    }

    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new IllegalArgumentException("Location not found with id: " + id);
        }
        locationRepository.deleteById(id);
    }
}
