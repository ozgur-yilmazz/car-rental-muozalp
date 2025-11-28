package com.alpozgurtuna.car_rental.repository;

import com.alpozgurtuna.car_rental.domain.Car;
import com.alpozgurtuna.car_rental.domain.CarStatus;
import com.alpozgurtuna.car_rental.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByBarcode(String barcode);

    Optional<Car> findByLicensePlate(String licensePlate);

    List<Car> findByStatus(CarStatus status);

    List<Car> findByLocation(Location location);

    List<Car> findByLocationAndStatus(Location location, CarStatus status);

    @Query("SELECT c FROM Car c WHERE c.location.id = :locationId AND c.status = :status")
    List<Car> findAvailableCarsAtLocation(@Param("locationId") Long locationId, @Param("status") CarStatus status);

    List<Car> findByBrand(String brand);

    @Query("SELECT c FROM Car c WHERE c.dailyPrice <= :maxPrice AND c.status = :status")
    List<Car> findByPriceRangeAndStatus(@Param("maxPrice") java.math.BigDecimal maxPrice,
            @Param("status") CarStatus status);
}
