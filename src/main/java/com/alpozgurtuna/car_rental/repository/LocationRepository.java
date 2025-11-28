package com.alpozgurtuna.car_rental.repository;

import com.alpozgurtuna.car_rental.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByCode(String code);

    Optional<Location> findByName(String name);

    boolean existsByCode(String code);
}
