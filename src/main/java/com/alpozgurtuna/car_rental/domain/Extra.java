package com.alpozgurtuna.car_rental.domain;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "extras")
@Getter
@Setter
@NoArgsConstructor
public class Extra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name; // e.g. GPS, Snow chains

    @Column(nullable = false)
    private BigDecimal price; // Single price (not daily)

    public Extra(Long id, String code, String name, BigDecimal price) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.price = price;
    }
}
