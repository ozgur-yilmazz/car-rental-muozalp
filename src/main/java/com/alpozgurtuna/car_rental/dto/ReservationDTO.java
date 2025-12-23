package com.alpozgurtuna.car_rental.dto;

import com.alpozgurtuna.car_rental.domain.ReservationStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservationDTO {
    private Long id;
    private String reservationNumber;
    private LocalDateTime creationDate;
    private LocalDateTime pickUpDateTime;
    private LocalDateTime dropOffDateTime;
    private LocalDateTime returnDateTime;
    private ReservationStatus status;
    private MemberDTO member;
    private CarDTO car;
    private LocationDTO pickUpLocation;
    private LocationDTO dropOffLocation;
    private List<ExtraDTO> extras;
    private BigDecimal totalAmount;
    private long reservationDayCount;
}
