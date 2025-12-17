package com.alpozgurtuna.car_rental.dto;

import com.alpozgurtuna.car_rental.domain.CarCategory;
import com.alpozgurtuna.car_rental.domain.CarStatus;
import com.alpozgurtuna.car_rental.domain.TransmissionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarDTO {
    private Long id;
    private String barcode;
    private String licensePlate;
    private String brand;
    private String model;
    private int numberOfSeats;
    private long mileage;
    private TransmissionType transmissionType;
    private BigDecimal dailyPrice;
    private CarCategory category;
    private CarStatus status;
    private LocationDTO location;
}
