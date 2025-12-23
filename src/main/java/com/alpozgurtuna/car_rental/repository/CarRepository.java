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

    @Query("SELECT c FROM Car c WHERE " +
            "(:category IS NULL OR c.category = :category) AND " +
            "(:transmissionType IS NULL OR c.transmissionType = :transmissionType) AND " +
            "(:minPrice IS NULL OR c.dailyPrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR c.dailyPrice <= :maxPrice) AND " +
            "(:numberOfSeats IS NULL OR c.numberOfSeats >= :numberOfSeats) AND " +
            "(:locationCode IS NULL OR c.location.code = :locationCode) AND " +
            "c.status = 'AVAILABLE' AND " +
            "NOT EXISTS (SELECT r FROM Reservation r WHERE r.car = c AND r.status = 'ACTIVE' AND " +
            "((r.pickUpDateTime < :dropOffDate) AND (r.dropOffDateTime > :pickUpDate)))")
    List<Car> searchAvailableCars(
            @Param("category") com.alpozgurtuna.car_rental.domain.CarCategory category,
            @Param("transmissionType") com.alpozgurtuna.car_rental.domain.TransmissionType transmissionType,
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice,
            @Param("numberOfSeats") Integer numberOfSeats,
            @Param("locationCode") String locationCode,
            @Param("pickUpDate") java.time.LocalDateTime pickUpDate,
            @Param("dropOffDate") java.time.LocalDateTime dropOffDate);
}
