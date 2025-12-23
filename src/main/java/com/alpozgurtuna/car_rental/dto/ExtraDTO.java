package com.alpozgurtuna.car_rental.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExtraDTO {
    private Long id;
    private String code;
    private String name;
    private BigDecimal price;
}
