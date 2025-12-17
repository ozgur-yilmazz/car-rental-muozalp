package com.alpozgurtuna.car_rental.controller;

import com.alpozgurtuna.car_rental.domain.Reservation;
import com.alpozgurtuna.car_rental.domain.ReservationStatus;
import com.alpozgurtuna.car_rental.dto.DtoMapper;
import com.alpozgurtuna.car_rental.dto.RentedCarDTO;
import com.alpozgurtuna.car_rental.dto.ReservationDTO;
import com.alpozgurtuna.car_rental.dto.ReservationRequestDTO;
import com.alpozgurtuna.car_rental.repository.ExtraRepository;
import com.alpozgurtuna.car_rental.repository.LocationRepository;
import com.alpozgurtuna.car_rental.service.CarService;
import com.alpozgurtuna.car_rental.service.MemberService;
import com.alpozgurtuna.car_rental.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final CarService carService;
    private final MemberService memberService;
    private final LocationRepository locationRepository;
    private final ExtraRepository extraRepository;
    private final DtoMapper dtoMapper;

    @PostMapping
    public ResponseEntity<ReservationDTO> makeReservation(@RequestBody ReservationRequestDTO request) {
        try {
            ReservationDTO created = reservationService.createReservation(request);
            return ResponseEntity.ok(created);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/rented")
    public ResponseEntity<List<RentedCarDTO>> getRentedCars() {
        List<Reservation> activeReservations = reservationService.getReservationsByStatus(ReservationStatus.ACTIVE);
        if (activeReservations.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(activeReservations.stream()
                .map(dtoMapper::toRentedCarDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping("/{reservationNumber}/extras")
    public ResponseEntity<Boolean> addExtra(@PathVariable String reservationNumber, @RequestParam String extraCode) {
        boolean result = reservationService.addExtra(reservationNumber, extraCode);
        if (result) {
            return ResponseEntity.ok(true);
        } else {
            if (extraRepository.findByCode(extraCode).isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(false);
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
