package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.Location;
import com.alpozgurtuna.car_rental.repository.CarRepository;
import com.alpozgurtuna.car_rental.repository.LocationRepository;
import com.alpozgurtuna.car_rental.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LocationServiceTest {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private LocationService locationService;

    private Location testLocation;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        reservationRepository.flush();
        carRepository.deleteAll();
        carRepository.flush();
        locationRepository.deleteAll();
        locationRepository.flush();
        
        testLocation = new Location();
        testLocation.setCode("IST_TEST");
        testLocation.setName("Istanbul Airport Test");
    }

    @Test
    void createLocation_Success() {
        Location result = locationService.createLocation(testLocation);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("IST_TEST", result.getCode());
        assertEquals("Istanbul Airport Test", result.getName());
    }

    @Test
    void createLocation_DuplicateCode_ThrowsException() {
        locationService.createLocation(testLocation);

        Location duplicate = new Location();
        duplicate.setCode("IST_TEST");
        duplicate.setName("Different Airport");

        assertThrows(IllegalArgumentException.class, () -> locationService.createLocation(duplicate));
    }

    @Test
    void getLocationById_Success() {
        Location saved = locationService.createLocation(testLocation);

        Location result = locationService.getLocationById(saved.getId());

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Istanbul Airport Test", result.getName());
    }

    @Test
    void getLocationById_NotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> locationService.getLocationById(999L));
    }

    @Test
    void getLocationByCode_Success() {
        locationService.createLocation(testLocation);

        Location result = locationService.getLocationByCode("IST_TEST");

        assertNotNull(result);
        assertEquals("IST_TEST", result.getCode());
    }

    @Test
    void getAllLocations_Success() {
        locationService.createLocation(testLocation);

        List<Location> result = locationService.getAllLocations();

        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertTrue(result.stream().anyMatch(l -> l.getName().equals("Istanbul Airport Test")));
    }

    @Test
    void updateLocation_Success() {
        Location saved = locationService.createLocation(testLocation);

        Location updatedLocation = new Location();
        updatedLocation.setCode("IST_TEST");
        updatedLocation.setName("Istanbul Sabiha Gokcen Airport");

        Location result = locationService.updateLocation(saved.getId(), updatedLocation);

        assertNotNull(result);
        assertEquals("Istanbul Sabiha Gokcen Airport", result.getName());
    }

    @Test
    void deleteLocation_Success() {
        Location saved = locationService.createLocation(testLocation);

        locationService.deleteLocation(saved.getId());

        assertThrows(IllegalArgumentException.class, () -> locationService.getLocationById(saved.getId()));
    }

    @Test
    void deleteLocation_NotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> locationService.deleteLocation(999L));
    }
}
