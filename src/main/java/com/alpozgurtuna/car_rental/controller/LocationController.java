package com.alpozgurtuna.car_rental.controller;

import com.alpozgurtuna.car_rental.domain.Location;
import com.alpozgurtuna.car_rental.dto.DtoMapper;
import com.alpozgurtuna.car_rental.dto.LocationDTO;
import com.alpozgurtuna.car_rental.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        List<Location> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations.stream()
                .map(dtoMapper::toLocationDTO)
                .collect(Collectors.toList()));
    }
}
