package com.alpozgurtuna.car_rental.dto;

import com.alpozgurtuna.car_rental.domain.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    public CarDTO toCarDTO(Car car) {
        if (car == null) return null;
        CarDTO dto = new CarDTO();
        dto.setId(car.getId());
        dto.setBarcode(car.getBarcode());
        dto.setLicensePlate(car.getLicensePlate());
        dto.setBrand(car.getBrand());
        dto.setModel(car.getModel());
        dto.setNumberOfSeats(car.getNumberOfSeats());
        dto.setMileage(car.getMileage());
        dto.setTransmissionType(car.getTransmissionType());
        dto.setDailyPrice(car.getDailyPrice());
        dto.setCategory(car.getCategory());
        dto.setStatus(car.getStatus());
        dto.setLocation(toLocationDTO(car.getLocation()));
        return dto;
    }

    public LocationDTO toLocationDTO(Location location) {
        if (location == null) return null;
        LocationDTO dto = new LocationDTO();
        dto.setId(location.getId());
        dto.setCode(location.getCode());
        dto.setName(location.getName());
        return dto;
    }

    public MemberDTO toMemberDTO(Member member) {
        if (member == null) return null;
        MemberDTO dto = new MemberDTO();
        dto.setId(member.getId());
        dto.setName(member.getName());
        dto.setAddress(member.getAddress());
        dto.setEmail(member.getEmail());
        dto.setPhone(member.getPhone());
        dto.setDrivingLicenseNumber(member.getDrivingLicenseNumber());
        return dto;
    }

    public ExtraDTO toExtraDTO(Extra extra) {
        if (extra == null) return null;
        ExtraDTO dto = new ExtraDTO();
        dto.setId(extra.getId());
        dto.setCode(extra.getCode());
        dto.setName(extra.getName());
        dto.setPrice(extra.getPrice());
        return dto;
    }

    public ReservationDTO toReservationDTO(Reservation reservation) {
        if (reservation == null) return null;
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setReservationNumber(reservation.getReservationNumber());
        dto.setCreationDate(reservation.getCreationDate());
        dto.setPickUpDateTime(reservation.getPickUpDateTime());
        dto.setDropOffDateTime(reservation.getDropOffDateTime());
        dto.setReturnDateTime(reservation.getReturnDateTime());
        dto.setStatus(reservation.getStatus());
        dto.setMember(toMemberDTO(reservation.getMember()));
        dto.setCar(toCarDTO(reservation.getCar()));
        dto.setPickUpLocation(toLocationDTO(reservation.getPickUpLocation()));
        dto.setDropOffLocation(toLocationDTO(reservation.getDropOffLocation()));
        if (reservation.getExtras() != null) {
            dto.setExtras(reservation.getExtras().stream().map(this::toExtraDTO).collect(Collectors.toList()));
        }
        
        // Calculate total amount
        long days = reservation.getReservationDayCount();
        BigDecimal carTotal = reservation.getCar().getDailyPrice().multiply(BigDecimal.valueOf(days));
        BigDecimal extrasTotal = BigDecimal.ZERO;
        if (reservation.getExtras() != null) {
            for (Extra extra : reservation.getExtras()) {
                extrasTotal = extrasTotal.add(extra.getPrice());
            }
        }
        dto.setTotalAmount(carTotal.add(extrasTotal));
        dto.setReservationDayCount(days);
        
        return dto;
    }
    
    public RentedCarDTO toRentedCarDTO(Reservation reservation) {
        if (reservation == null) return null;
        RentedCarDTO dto = new RentedCarDTO();
        dto.setBrand(reservation.getCar().getBrand());
        dto.setModel(reservation.getCar().getModel());
        dto.setCarType(reservation.getCar().getCategory());
        dto.setTransmissionType(reservation.getCar().getTransmissionType());
        dto.setBarcode(reservation.getCar().getBarcode());
        dto.setReservationNumber(reservation.getReservationNumber());
        dto.setMemberName(reservation.getMember().getName());
        dto.setDropOffDateTime(reservation.getDropOffDateTime());
        dto.setDropOffLocationName(reservation.getDropOffLocation().getName());
        dto.setReservationDayCount(reservation.getReservationDayCount());
        return dto;
    }
}
