package com.alpozgurtuna.car_rental.repository;

import com.alpozgurtuna.car_rental.domain.Reservation;
import com.alpozgurtuna.car_rental.domain.ReservationStatus;
import com.alpozgurtuna.car_rental.domain.Car;
import com.alpozgurtuna.car_rental.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationNumber(String reservationNumber);

    List<Reservation> findByMember(Member member);

    List<Reservation> findByMemberId(Long memberId);

    List<Reservation> findByCar(Car car);

    List<Reservation> findByCarId(Long carId);

    List<Reservation> findByStatus(ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.member.id = :memberId AND r.status = :status")
    List<Reservation> findByMemberIdAndStatus(@Param("memberId") Long memberId,
            @Param("status") ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.car.id = :carId AND r.status = :status " +
            "AND ((r.pickUpDateTime BETWEEN :startDate AND :endDate) " +
            "OR (r.dropOffDateTime BETWEEN :startDate AND :endDate) " +
            "OR (:startDate BETWEEN r.pickUpDateTime AND r.dropOffDateTime))")
    List<Reservation> findConflictingReservations(
            @Param("carId") Long carId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.pickUpDateTime >= :startDate AND r.pickUpDateTime < :endDate")
    List<Reservation> findReservationsByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
