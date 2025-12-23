package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.*;
import com.alpozgurtuna.car_rental.dto.CarDTO;
import com.alpozgurtuna.car_rental.repository.CarRepository;
import com.alpozgurtuna.car_rental.repository.LocationRepository;
import com.alpozgurtuna.car_rental.repository.MemberRepository;
import com.alpozgurtuna.car_rental.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CarServiceTest {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CarService carService;

    private Car testCar;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        reservationRepository.deleteAll();
        reservationRepository.flush();
        carRepository.deleteAll();
        carRepository.flush();
        locationRepository.deleteAll();
        locationRepository.flush();
        
        // Create and save test location
        testLocation = new Location();
        testLocation.setCode("IST_TEST");
        testLocation.setName("Istanbul Airport Test");
        testLocation = locationRepository.save(testLocation);

        // Create test car
        testCar = new Car();
        testCar.setBarcode("CAR001");
        testCar.setLicensePlate("34ABC123");
        testCar.setBrand("Toyota");
        testCar.setModel("Corolla");
        testCar.setNumberOfSeats(5);
        testCar.setMileage(15000L);
        testCar.setTransmissionType(TransmissionType.AUTOMATIC);
        testCar.setDailyPrice(new BigDecimal("350.00"));
        testCar.setCategory(CarCategory.SEDAN);
        testCar.setStatus(CarStatus.AVAILABLE);
        testCar.setLocation(testLocation);
    }

    @Test
    void createCar_Success() {
        Car result = carService.createCar(testCar);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("CAR001", result.getBarcode());
        assertEquals("Toyota", result.getBrand());
    }

    @Test
    void createCar_DuplicateBarcode_ThrowsException() {
        carService.createCar(testCar);

        Car duplicateCar = new Car();
        duplicateCar.setBarcode("CAR001");
        duplicateCar.setLicensePlate("34XYZ999");
        duplicateCar.setBrand("Honda");
        duplicateCar.setModel("Civic");
        duplicateCar.setNumberOfSeats(5);
        duplicateCar.setMileage(10000L);
        duplicateCar.setTransmissionType(TransmissionType.MANUAL);
        duplicateCar.setDailyPrice(new BigDecimal("300.00"));
        duplicateCar.setCategory(CarCategory.SEDAN);
        duplicateCar.setStatus(CarStatus.AVAILABLE);
        duplicateCar.setLocation(testLocation);

        assertThrows(IllegalArgumentException.class, () -> carService.createCar(duplicateCar));
    }

    @Test
    void createCar_DuplicateLicensePlate_ThrowsException() {
        carService.createCar(testCar);

        Car duplicateCar = new Car();
        duplicateCar.setBarcode("CAR002");
        duplicateCar.setLicensePlate("34ABC123");
        duplicateCar.setBrand("Honda");
        duplicateCar.setModel("Civic");
        duplicateCar.setNumberOfSeats(5);
        duplicateCar.setMileage(10000L);
        duplicateCar.setTransmissionType(TransmissionType.MANUAL);
        duplicateCar.setDailyPrice(new BigDecimal("300.00"));
        duplicateCar.setCategory(CarCategory.SEDAN);
        duplicateCar.setStatus(CarStatus.AVAILABLE);
        duplicateCar.setLocation(testLocation);

        assertThrows(IllegalArgumentException.class, () -> carService.createCar(duplicateCar));
    }

    @Test
    void getCarById_Success() {
        Car saved = carService.createCar(testCar);

        Car result = carService.getCarById(saved.getId());

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Toyota", result.getBrand());
    }

    @Test
    void getCarById_NotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> carService.getCarById(999L));
    }

    @Test
    void getCarByBarcode_Success() {
        carService.createCar(testCar);

        Car result = carService.getCarByBarcode("CAR001");

        assertNotNull(result);
        assertEquals("CAR001", result.getBarcode());
    }

    @Test
    void getAllCars_Success() {
        carService.createCar(testCar);

        List<Car> result = carService.getAllCars();

        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertTrue(result.stream().anyMatch(c -> c.getBrand().equals("Toyota")));
    }

    @Test
    void getCarsByStatus_Success() {
        carService.createCar(testCar);

        List<Car> result = carService.getCarsByStatus(CarStatus.AVAILABLE);

        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertTrue(result.stream().allMatch(car -> car.getStatus() == CarStatus.AVAILABLE));
    }

    @Test
    void getAvailableCars_Success() {
        carService.createCar(testCar);

        List<Car> result = carService.getAvailableCars();

        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertTrue(result.stream().allMatch(car -> car.getStatus() == CarStatus.AVAILABLE));
    }

    @Test
    void getCarsByLocation_Success() {
        carService.createCar(testCar);

        List<Car> result = carService.getCarsByLocation(testLocation);

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void getAvailableCarsAtLocation_Success() {
        Car saved = carService.createCar(testCar);

        List<Car> result = carService.getAvailableCarsAtLocation(testLocation.getId());

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void getCarsByBrand_Success() {
        carService.createCar(testCar);

        List<Car> result = carService.getCarsByBrand("Toyota");

        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertTrue(result.stream().allMatch(c -> c.getBrand().equals("Toyota")));
    }

    @Test
    void updateCar_Success() {
        Car saved = carService.createCar(testCar);

        Car updatedCar = new Car();
        updatedCar.setBarcode("CAR001");
        updatedCar.setLicensePlate("34ABC123");
        updatedCar.setBrand("Honda");
        updatedCar.setModel("Civic");
        updatedCar.setNumberOfSeats(5);
        updatedCar.setMileage(20000L);
        updatedCar.setTransmissionType(TransmissionType.MANUAL);
        updatedCar.setDailyPrice(new BigDecimal("400.00"));
        updatedCar.setCategory(CarCategory.SEDAN);
        updatedCar.setStatus(CarStatus.AVAILABLE);
        updatedCar.setLocation(testLocation);

        Car result = carService.updateCar(saved.getId(), updatedCar);

        assertNotNull(result);
        assertEquals("Honda", result.getBrand());
        assertEquals("Civic", result.getModel());
    }

    @Test
    void updateCarStatus_Success() {
        Car saved = carService.createCar(testCar);

        carService.updateCarStatus(saved.getId(), CarStatus.RESERVED);

        Car updated = carService.getCarById(saved.getId());
        assertEquals(CarStatus.RESERVED, updated.getStatus());
    }

    @Test
    void updateCarLocation_Success() {
        Car saved = carService.createCar(testCar);
        
        Location newLocation = new Location();
        newLocation.setCode("ANK_TEST");
        newLocation.setName("Ankara Airport Test");
        newLocation = locationRepository.save(newLocation);

        carService.updateCarLocation(saved.getId(), newLocation);

        Car updated = carService.getCarById(saved.getId());
        assertEquals("ANK_TEST", updated.getLocation().getCode());
    }

    @Test
    void deleteCar_Success() {
        carService.createCar(testCar);

        boolean result = carService.deleteCar("CAR001");

        assertTrue(result);
        assertThrows(IllegalArgumentException.class, () -> carService.getCarByBarcode("CAR001"));
    }

    @Test
    void deleteCar_WithReservations_ReturnsFalse() {
        Car saved = carService.createCar(testCar);
        
        // Create a reservation for this car
        Member member = new Member();
        member.setName("Test User");
        member.setEmail("test.delete.car@test.com");
        member.setPhone("1234567890");
        member.setAddress("Test Address");
        member.setDrivingLicenseNumber("DL123456_TEST");
        member = memberRepository.save(member);
        
        Reservation reservation = new Reservation();
        reservation.setReservationNumber("TEST123");
        reservation.setCar(saved);
        reservation.setMember(member);
        reservation.setPickUpDateTime(LocalDateTime.now().plusDays(1));
        reservation.setDropOffDateTime(LocalDateTime.now().plusDays(3));
        reservation.setPickUpLocation(testLocation);
        reservation.setDropOffLocation(testLocation);
        reservation.setCreationDate(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservationRepository.save(reservation);

        boolean result = carService.deleteCar("CAR001");

        assertFalse(result);
    }

    @Test
    void searchCars_Success() {
        carService.createCar(testCar);

        List<CarDTO> result = carService.searchCars(
            null, 
            null, 
            null, 
            null, 
            null, 
            "IST_TEST", 
            LocalDateTime.now().plusDays(1), 
            LocalDateTime.now().plusDays(3)
        );

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }
}
