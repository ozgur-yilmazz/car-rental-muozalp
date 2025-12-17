package com.alpozgurtuna.car_rental.dto;

import com.alpozgurtuna.car_rental.domain.CarCategory;
import com.alpozgurtuna.car_rental.domain.TransmissionType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RentedCarDTO {
    private String brand;
    private String model;
    private CarCategory carType;
    private TransmissionType transmissionType;
    private String barcode;
    private String reservationNumber;
    private String memberName;
    private LocalDateTime dropOffDateTime;
    private String dropOffLocationName;
    private long reservationDayCount;
}
