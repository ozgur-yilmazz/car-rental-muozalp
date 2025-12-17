package com.alpozgurtuna.car_rental.domain;

import java.util.Objects;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // e.g. "1", "2"

    @Column(nullable = false)
    private String name; // e.g. "İstanbul Airport"

    public Location(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }
}
