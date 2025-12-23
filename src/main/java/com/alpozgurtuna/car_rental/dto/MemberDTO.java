package com.alpozgurtuna.car_rental.dto;

import lombok.Data;

@Data
public class MemberDTO {
    private Long id;
    private String name;
    private String address;
    private String email;
    private String phone;
    private String drivingLicenseNumber;
}
