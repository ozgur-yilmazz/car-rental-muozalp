package com.alpozgurtuna.car_rental.controller;

import com.alpozgurtuna.car_rental.dto.RentedCarDTO;
import com.alpozgurtuna.car_rental.dto.ReservationDTO;
import com.alpozgurtuna.car_rental.dto.ReservationRequestDTO;
import com.alpozgurtuna.car_rental.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationDTO> makeReservation(@RequestBody ReservationRequestDTO request) {
        try {
            ReservationDTO created = reservationService.createReservation(request);
            return ResponseEntity.ok(created);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/rented")
    public ResponseEntity<List<RentedCarDTO>> getRentedCars() {
        List<RentedCarDTO> rentedCars = reservationService.getRentedCars();
        if (rentedCars.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rentedCars);
    }

    @PostMapping("/{reservationNumber}/extras")
    public ResponseEntity<Boolean> addExtra(@PathVariable String reservationNumber, @RequestParam String extraCode) {
        boolean result = reservationService.addExtra(reservationNumber, extraCode);
        if (result) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{reservationNumber}/return")
    public ResponseEntity<Boolean> returnCar(@PathVariable String reservationNumber) {
        boolean result = reservationService.returnCar(reservationNumber);
        if (result) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{reservationNumber}/cancel")
    public ResponseEntity<Boolean> cancelReservation(@PathVariable String reservationNumber) {
        boolean result = reservationService.cancelReservation(reservationNumber);
        if (result) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{reservationNumber}")
    public ResponseEntity<Boolean> deleteReservation(@PathVariable String reservationNumber) {
        boolean result = reservationService.deleteReservation(reservationNumber);
        if (result) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
