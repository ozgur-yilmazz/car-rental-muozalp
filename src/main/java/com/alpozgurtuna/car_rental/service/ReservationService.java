package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.*;
import com.alpozgurtuna.car_rental.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CarService carService;
    private final MemberService memberService;

    public Reservation createReservation(Reservation reservation) {
        // Validate car availability
        Car car = carService.getCarById(reservation.getCar().getId());
        if (car.getStatus() != CarStatus.AVAILABLE) {
            throw new IllegalStateException("Car is not available for reservation");
        }

        // Check for conflicting reservations
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                car.getId(),
                reservation.getPickUpDateTime(),
                reservation.getDropOffDateTime(),
                ReservationStatus.ACTIVE);

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Car is already reserved for the selected dates");
        }

        // Validate dates
        if (reservation.getPickUpDateTime().isAfter(reservation.getDropOffDateTime())) {
            throw new IllegalArgumentException("Pick-up date must be before drop-off date");
        }

        if (reservation.getPickUpDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Pick-up date cannot be in the past");
        }

        // Generate reservation number
        reservation.setReservationNumber(generateReservationNumber());
        reservation.setCreationDate(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.ACTIVE);

        // Update car status
        carService.updateCarStatus(car.getId(), CarStatus.RESERVED);

        return reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Reservation getReservationByNumber(String reservationNumber) {
        return reservationRepository.findByReservationNumber(reservationNumber)
                .orElseThrow(
                        () -> new IllegalArgumentException("Reservation not found with number: " + reservationNumber));
    }

    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByMember(Long memberId) {
        return reservationRepository.findByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByCar(Long carId) {
        return reservationRepository.findByCarId(carId);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getActiveReservationsByMember(Long memberId) {
        return reservationRepository.findByMemberIdAndStatus(memberId, ReservationStatus.ACTIVE);
    }

    public Reservation completeReservation(Long reservationId, LocalDateTime returnDateTime) {
        Reservation reservation = getReservationById(reservationId);

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Only active reservations can be completed");
        }

        reservation.setReturnDateTime(returnDateTime);
        reservation.setStatus(ReservationStatus.COMPLETED);

        // Update car status and location if needed
        Car car = reservation.getCar();
        car.setStatus(CarStatus.AVAILABLE);
        car.setLocation(reservation.getDropOffLocation());
        carService.updateCar(car.getId(), car);

        return reservationRepository.save(reservation);
    }

    public Reservation cancelReservation(Long reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed reservation");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        // Make car available again
        carService.updateCarStatus(reservation.getCar().getId(), CarStatus.AVAILABLE);

        return reservationRepository.save(reservation);
    }

    public Reservation updateReservation(Long id, Reservation reservationDetails) {
        Reservation reservation = getReservationById(id);

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Only active reservations can be updated");
        }

        // Validate new dates
        if (reservationDetails.getPickUpDateTime().isAfter(reservationDetails.getDropOffDateTime())) {
            throw new IllegalArgumentException("Pick-up date must be before drop-off date");
        }

        // Check for conflicts if dates or car changed
        if (!reservation.getCar().getId().equals(reservationDetails.getCar().getId()) ||
                !reservation.getPickUpDateTime().equals(reservationDetails.getPickUpDateTime()) ||
                !reservation.getDropOffDateTime().equals(reservationDetails.getDropOffDateTime())) {

            List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                    reservationDetails.getCar().getId(),
                    reservationDetails.getPickUpDateTime(),
                    reservationDetails.getDropOffDateTime(),
                    ReservationStatus.ACTIVE);

            // Remove current reservation from conflicts
            conflicts.removeIf(r -> r.getId().equals(reservation.getId()));

            if (!conflicts.isEmpty()) {
                throw new IllegalStateException("Car is already reserved for the selected dates");
            }
        }

        reservation.setPickUpDateTime(reservationDetails.getPickUpDateTime());
        reservation.setDropOffDateTime(reservationDetails.getDropOffDateTime());
        reservation.setPickUpLocation(reservationDetails.getPickUpLocation());
        reservation.setDropOffLocation(reservationDetails.getDropOffLocation());
        reservation.setExtras(reservationDetails.getExtras());

        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        Reservation reservation = getReservationById(id);

        if (reservation.getStatus() == ReservationStatus.ACTIVE) {
            carService.updateCarStatus(reservation.getCar().getId(), CarStatus.AVAILABLE);
        }

        reservationRepository.deleteById(id);
    }

    private String generateReservationNumber() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
