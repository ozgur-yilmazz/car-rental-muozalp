package com.alpozgurtuna.car_rental.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservationRequestDTO {
    private String carBarcode;
    private LocalDateTime pickUpDateTime;
    private LocalDateTime dropOffDateTime;
    private Long memberId;
    private String pickUpLocationCode;
    private String dropOffLocationCode;
    private List<String> extraCodes; // List of extra codes
}
