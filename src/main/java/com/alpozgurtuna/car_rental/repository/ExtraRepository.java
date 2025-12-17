package com.alpozgurtuna.car_rental.repository;

import com.alpozgurtuna.car_rental.domain.Extra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtraRepository extends JpaRepository<Extra, Long> {

    Optional<Extra> findByName(String name);

    Optional<Extra> findByCode(String code);

    List<Extra> findByPriceLessThanEqual(java.math.BigDecimal maxPrice);
}
