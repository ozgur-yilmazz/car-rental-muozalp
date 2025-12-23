package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.Car;
import com.alpozgurtuna.car_rental.domain.CarCategory;
import com.alpozgurtuna.car_rental.domain.CarStatus;
import com.alpozgurtuna.car_rental.domain.Location;
import com.alpozgurtuna.car_rental.domain.TransmissionType;
import com.alpozgurtuna.car_rental.dto.CarDTO;
import com.alpozgurtuna.car_rental.dto.DtoMapper;
import com.alpozgurtuna.car_rental.repository.CarRepository;
import com.alpozgurtuna.car_rental.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CarService {

    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;
    private final DtoMapper dtoMapper;

    public Car createCar(Car car) {
        if (carRepository.findByBarcode(car.getBarcode()).isPresent()) {
            throw new IllegalArgumentException("Car with barcode " + car.getBarcode() + " already exists");
        }
        if (carRepository.findByLicensePlate(car.getLicensePlate()).isPresent()) {
            throw new IllegalArgumentException("Car with license plate " + car.getLicensePlate() + " already exists");
        }
        return carRepository.save(car);
    }

    @Transactional(readOnly = true)
    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Car getCarByBarcode(String barcode) {
        return carRepository.findByBarcode(barcode)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with barcode: " + barcode));
    }

    @Transactional(readOnly = true)
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Car> getCarsByStatus(CarStatus status) {
        return carRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Car> getAvailableCars() {
        return carRepository.findByStatus(CarStatus.AVAILABLE);
    }

    @Transactional(readOnly = true)
    public List<Car> getCarsByLocation(Location location) {
        return carRepository.findByLocation(location);
    }

    @Transactional(readOnly = true)
    public List<Car> getAvailableCarsAtLocation(Long locationId) {
        return carRepository.findAvailableCarsAtLocation(locationId, CarStatus.AVAILABLE);
    }

    @Transactional(readOnly = true)
    public List<Car> getCarsByBrand(String brand) {
        return carRepository.findByBrand(brand);
    }

    @Transactional(readOnly = true)
    public List<Car> getCarsByMaxPrice(BigDecimal maxPrice) {
        return carRepository.findByPriceRangeAndStatus(maxPrice, CarStatus.AVAILABLE);
    }

    public Car updateCar(Long id, Car carDetails) {
        Car car = getCarById(id);

        if (!car.getBarcode().equals(carDetails.getBarcode()) &&
                carRepository.findByBarcode(carDetails.getBarcode()).isPresent()) {
            throw new IllegalArgumentException("Car with barcode " + carDetails.getBarcode() + " already exists");
        }

        if (!car.getLicensePlate().equals(carDetails.getLicensePlate()) &&
                carRepository.findByLicensePlate(carDetails.getLicensePlate()).isPresent()) {
            throw new IllegalArgumentException(
                    "Car with license plate " + carDetails.getLicensePlate() + " already exists");
        }

        car.setBarcode(carDetails.getBarcode());
        car.setLicensePlate(carDetails.getLicensePlate());
        car.setBrand(carDetails.getBrand());
        car.setModel(carDetails.getModel());
        car.setNumberOfSeats(carDetails.getNumberOfSeats());
        car.setMileage(carDetails.getMileage());
        car.setTransmissionType(carDetails.getTransmissionType());
        car.setDailyPrice(carDetails.getDailyPrice());
        car.setCategory(carDetails.getCategory());
        car.setStatus(carDetails.getStatus());
        car.setLocation(carDetails.getLocation());

        return carRepository.save(car);
    }

    public void updateCarStatus(Long carId, CarStatus status) {
        Car car = getCarById(carId);
        car.setStatus(status);
        carRepository.save(car);
    }

    public void updateCarLocation(Long carId, Location location) {
        Car car = getCarById(carId);
        car.setLocation(location);
        carRepository.save(car);
    }

    public boolean deleteCar(String barcode) {
        Car car = getCarByBarcode(barcode);
        if (!reservationRepository.findByCarId(car.getId()).isEmpty()) {
            return false;
        }
        carRepository.delete(car);
        return true;
    }

    @Transactional(readOnly = true)
    public List<CarDTO> searchCars(CarCategory category, TransmissionType transmissionType, BigDecimal minPrice,
                                BigDecimal maxPrice, Integer numberOfSeats, String locationCode,
                                LocalDateTime pickUpDate, LocalDateTime dropOffDate) {
        List<Car> cars = carRepository.searchAvailableCars(category, transmissionType, minPrice, maxPrice, numberOfSeats, locationCode, pickUpDate, dropOffDate);
        return cars.stream().map(dtoMapper::toCarDTO).collect(Collectors.toList());
    }
}
