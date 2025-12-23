package com.alpozgurtuna.car_rental.domain;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.*;   // Jpa Persistence API  
import lombok.Getter;  // for getter methods
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cars")
@Getter
@Setter
@NoArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String barcode;

    @Column(unique = true, nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    private int numberOfSeats;

    private long mileage;

    @Enumerated(EnumType.STRING)
    private TransmissionType transmissionType;

    @Column(nullable = false)
    private BigDecimal dailyPrice;

    @Enumerated(EnumType.STRING)
    private CarCategory category;

    @Enumerated(EnumType.STRING)
    private CarStatus status = CarStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location; // current location
}
