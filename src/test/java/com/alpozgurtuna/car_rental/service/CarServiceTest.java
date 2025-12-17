package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.*;
import com.alpozgurtuna.car_rental.dto.CarDTO;
import com.alpozgurtuna.car_rental.dto.DtoMapper;
import com.alpozgurtuna.car_rental.repository.CarRepository;
import com.alpozgurtuna.car_rental.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private CarService carService;

    private Car testCar;
    private CarDTO testCarDTO;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        testLocation = new Location(1L, "IST", "Istanbul Airport");

        testCar = new Car();
        testCar.setId(1L);
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

        testCarDTO = new CarDTO();
        testCarDTO.setBarcode("CAR001");
        testCarDTO.setBrand("Toyota");
    }

    @Test
    void createCar_Success() {
        when(carRepository.findByBarcode(anyString())).thenReturn(Optional.empty());
        when(carRepository.findByLicensePlate(anyString())).thenReturn(Optional.empty());
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        Car result = carService.createCar(testCar);

        assertNotNull(result);
        assertEquals("CAR001", result.getBarcode());
        assertEquals("Toyota", result.getBrand());
        verify(carRepository, times(1)).save(testCar);
    }

    @Test
    void createCar_DuplicateBarcode_ThrowsException() {
        when(carRepository.findByBarcode(anyString())).thenReturn(Optional.of(testCar));

        assertThrows(IllegalArgumentException.class, () -> carService.createCar(testCar));
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void createCar_DuplicateLicensePlate_ThrowsException() {
        when(carRepository.findByBarcode(anyString())).thenReturn(Optional.empty());
        when(carRepository.findByLicensePlate(anyString())).thenReturn(Optional.of(testCar));

        assertThrows(IllegalArgumentException.class, () -> carService.createCar(testCar));
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void getCarById_Success() {
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(testCar));

        Car result = carService.getCarById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Toyota", result.getBrand());
    }

    @Test
    void getCarById_NotFound_ThrowsException() {
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> carService.getCarById(999L));
    }

    @Test
    void getCarByBarcode_Success() {
        when(carRepository.findByBarcode(anyString())).thenReturn(Optional.of(testCar));

        Car result = carService.getCarByBarcode("CAR001");

        assertNotNull(result);
        assertEquals("CAR001", result.getBarcode());
    }

    @Test
    void getAllCars_Success() {
        List<Car> cars = Arrays.asList(testCar);
        when(carRepository.findAll()).thenReturn(cars);

        List<Car> result = carService.getAllCars();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
    }

    @Test
    void getCarsByStatus_Success() {
        List<Car> cars = Arrays.asList(testCar);
        when(carRepository.findByStatus(any(CarStatus.class))).thenReturn(cars);

        List<Car> result = carService.getCarsByStatus(CarStatus.AVAILABLE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CarStatus.AVAILABLE, result.get(0).getStatus());
    }

    @Test
    void getAvailableCars_Success() {
        List<Car> cars = Arrays.asList(testCar);
        when(carRepository.findByStatus(CarStatus.AVAILABLE)).thenReturn(cars);

        List<Car> result = carService.getAvailableCars();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(car -> car.getStatus() == CarStatus.AVAILABLE));
    }

    @Test
    void getCarsByLocation_Success() {
        List<Car> cars = Arrays.asList(testCar);
        when(carRepository.findByLocation(any(Location.class))).thenReturn(cars);

        List<Car> result = carService.getCarsByLocation(testLocation);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAvailableCarsAtLocation_Success() {
        List<Car> cars = Arrays.asList(testCar);
        when(carRepository.findAvailableCarsAtLocation(anyLong(), any(CarStatus.class))).thenReturn(cars);

        List<Car> result = carService.getAvailableCarsAtLocation(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getCarsByBrand_Success() {
        List<Car> cars = Arrays.asList(testCar);
        when(carRepository.findByBrand(anyString())).thenReturn(cars);

        List<Car> result = carService.getCarsByBrand("Toyota");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
    }

    @Test
    void updateCar_Success() {
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

        when(carRepository.findById(anyLong())).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(updatedCar);

        Car result = carService.updateCar(1L, updatedCar);

        assertNotNull(result);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void updateCarStatus_Success() {
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        carService.updateCarStatus(1L, CarStatus.RESERVED);

        verify(carRepository, times(1)).save(testCar);
    }

    @Test
    void updateCarLocation_Success() {
        Location newLocation = new Location(2L, "ANK", "Ankara Airport");
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        carService.updateCarLocation(1L, newLocation);

        verify(carRepository, times(1)).save(testCar);
    }

    @Test
    void deleteCar_Success() {
        when(carRepository.findByBarcode(anyString())).thenReturn(Optional.of(testCar));
        when(reservationRepository.findByCarId(anyLong())).thenReturn(new ArrayList<>());
        doNothing().when(carRepository).delete(any(Car.class));

        boolean result = carService.deleteCar("CAR001");

        assertTrue(result);
        verify(carRepository, times(1)).delete(testCar);
    }

    @Test
    void deleteCar_WithReservations_ReturnsFalse() {
        when(carRepository.findByBarcode(anyString())).thenReturn(Optional.of(testCar));
        when(reservationRepository.findByCarId(anyLong())).thenReturn(Arrays.asList(new Reservation()));

        boolean result = carService.deleteCar("CAR001");

        assertFalse(result);
        verify(carRepository, never()).delete(any(Car.class));
    }

    @Test
    void searchCars_Success() {
        when(carRepository.searchAvailableCars(any(), any(), any(), any(), any(), anyString(), any(), any()))
                .thenReturn(Arrays.asList(testCar));
        when(dtoMapper.toCarDTO(any(Car.class))).thenReturn(testCarDTO);

        List<CarDTO> result = carService.searchCars(null, null, null, null, null, "IST", LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CAR001", result.get(0).getBarcode());
    }
}
