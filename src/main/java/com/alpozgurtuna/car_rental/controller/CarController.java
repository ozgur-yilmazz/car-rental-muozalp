package com.alpozgurtuna.car_rental.controller;

import com.alpozgurtuna.car_rental.domain.Car;
import com.alpozgurtuna.car_rental.domain.CarCategory;
import com.alpozgurtuna.car_rental.domain.TransmissionType;
import com.alpozgurtuna.car_rental.dto.CarDTO;
import com.alpozgurtuna.car_rental.dto.DtoMapper;
import com.alpozgurtuna.car_rental.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    private final DtoMapper dtoMapper;

    @GetMapping("/search")
    public ResponseEntity<List<CarDTO>> searchCars(
            @RequestParam(required = false) CarCategory category,
            @RequestParam(required = false) TransmissionType transmissionType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer numberOfSeats,
            @RequestParam String locationCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickUpDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dropOffDate) {

        List<CarDTO> cars = carService.searchCars(category, transmissionType, minPrice, maxPrice, numberOfSeats,
                locationCode, pickUpDate, dropOffDate);
        if (cars.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cars);
    }

    @DeleteMapping("/{barcode}")
    public ResponseEntity<Void> deleteCar(@PathVariable String barcode) {
        try {
            boolean deleted = carService.deleteCar(barcode);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
