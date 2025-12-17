package com.alpozgurtuna.car_rental.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reservationNumber;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private LocalDateTime pickUpDateTime;

    @Column(nullable = false)
    private LocalDateTime dropOffDateTime;

    private LocalDateTime returnDateTime;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_location_id", nullable = false)
    private Location pickUpLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dropoff_location_id", nullable = false)
    private Location dropOffLocation;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "reservation_extras", joinColumns = @JoinColumn(name = "reservation_id"), inverseJoinColumns = @JoinColumn(name = "extra_id"))
    private List<Extra> extras = new ArrayList<>();

    public long getReservationDayCount() {
        if (pickUpDateTime == null || dropOffDateTime == null)
            return 0L;
        return java.time.Duration.between(pickUpDateTime, dropOffDateTime).toDays();
    }
}
