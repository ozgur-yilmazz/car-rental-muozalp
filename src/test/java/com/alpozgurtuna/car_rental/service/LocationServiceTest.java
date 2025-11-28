package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.Location;
import com.alpozgurtuna.car_rental.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    private Location testLocation;

    @BeforeEach
    void setUp() {
        testLocation = new Location();
        testLocation.setId(1L);
        testLocation.setCode("IST");
        testLocation.setName("Istanbul Airport");
    }

    @Test
    void createLocation_Success() {
        when(locationRepository.existsByCode(anyString())).thenReturn(false);
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);

        Location result = locationService.createLocation(testLocation);

        assertNotNull(result);
        assertEquals("IST", result.getCode());
        assertEquals("Istanbul Airport", result.getName());
        verify(locationRepository, times(1)).save(testLocation);
    }

    @Test
    void createLocation_DuplicateCode_ThrowsException() {
        when(locationRepository.existsByCode(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> locationService.createLocation(testLocation));
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void getLocationById_Success() {
        when(locationRepository.findById(anyLong())).thenReturn(Optional.of(testLocation));

        Location result = locationService.getLocationById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Istanbul Airport", result.getName());
    }

    @Test
    void getLocationById_NotFound_ThrowsException() {
        when(locationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> locationService.getLocationById(999L));
    }

    @Test
    void getLocationByCode_Success() {
        when(locationRepository.findByCode(anyString())).thenReturn(Optional.of(testLocation));

        Location result = locationService.getLocationByCode("IST");

        assertNotNull(result);
        assertEquals("IST", result.getCode());
    }

    @Test
    void getAllLocations_Success() {
        List<Location> locations = Arrays.asList(testLocation);
        when(locationRepository.findAll()).thenReturn(locations);

        List<Location> result = locationService.getAllLocations();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Istanbul Airport", result.get(0).getName());
    }

    @Test
    void updateLocation_Success() {
        Location updatedLocation = new Location();
        updatedLocation.setCode("IST");
        updatedLocation.setName("Istanbul Sabiha Gokcen Airport");

        when(locationRepository.findById(anyLong())).thenReturn(Optional.of(testLocation));
        when(locationRepository.save(any(Location.class))).thenReturn(updatedLocation);

        Location result = locationService.updateLocation(1L, updatedLocation);

        assertNotNull(result);
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void deleteLocation_Success() {
        when(locationRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(locationRepository).deleteById(anyLong());

        locationService.deleteLocation(1L);

        verify(locationRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteLocation_NotFound_ThrowsException() {
        when(locationRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> locationService.deleteLocation(999L));
        verify(locationRepository, never()).deleteById(anyLong());
    }
}
