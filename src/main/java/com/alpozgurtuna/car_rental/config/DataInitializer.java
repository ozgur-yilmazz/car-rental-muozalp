package com.alpozgurtuna.car_rental.config;

import com.alpozgurtuna.car_rental.domain.*;
import com.alpozgurtuna.car_rental.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final LocationService locationService;
    private final ExtraService extraService;
    private final MemberService memberService;
    private final CarService carService;
    private final ReservationService reservationService;

    @Override
    public void run(String... args) {
        log.info("Starting data initialization...");

        try {
            // Initialize Locations
            Location istanbul = createLocation("IST", "Istanbul Airport");
            Location ankara = createLocation("ANK", "Ankara Esenboga Airport");
            Location izmir = createLocation("IZM", "Izmir Adnan Menderes Airport");
            Location antalya = createLocation("AYT", "Antalya Airport");

            log.info("Created {} locations", 4);

            // Initialize Extras
            Extra gps = createExtra("GPS Navigation System", new BigDecimal("50.00"));
            Extra childSeat = createExtra("Child Safety Seat", new BigDecimal("30.00"));
            Extra snowChains = createExtra("Snow Chains", new BigDecimal("40.00"));
            Extra additionalDriver = createExtra("Additional Driver", new BigDecimal("100.00"));
            Extra wifi = createExtra("Mobile WiFi Hotspot", new BigDecimal("25.00"));

            log.info("Created {} extras", 5);

            // Initialize Members
            Member member1 = createMember("Ahmet Yılmaz", "Istanbul, Turkey",
                    "ahmet.yilmaz@email.com", "+90-532-111-2233", "TR123456789");
            Member member2 = createMember("Ayşe Demir", "Ankara, Turkey",
                    "ayse.demir@email.com", "+90-533-222-3344", "TR987654321");
            Member member3 = createMember("Mehmet Kaya", "Izmir, Turkey",
                    "mehmet.kaya@email.com", "+90-534-333-4455", "TR456789123");
            Member member4 = createMember("Fatma Öztürk", "Antalya, Turkey",
                    "fatma.ozturk@email.com", "+90-535-444-5566", "TR789123456");

            log.info("Created {} members", 4);

            // Initialize Cars
            Car car1 = createCar("CAR001", "34ABC123", "Toyota", "Corolla", 5, 15000L,
                    TransmissionType.AUTOMATIC, new BigDecimal("350.00"), CarCategory.SEDAN, istanbul);

            Car car2 = createCar("CAR002", "06DEF456", "Volkswagen", "Golf", 5, 20000L,
                    TransmissionType.MANUAL, new BigDecimal("300.00"), CarCategory.COMPACT, ankara);

            Car car3 = createCar("CAR003", "35GHI789", "BMW", "X5", 7, 10000L,
                    TransmissionType.AUTOMATIC, new BigDecimal("800.00"), CarCategory.SUV, izmir);

            Car car4 = createCar("CAR004", "07JKL012", "Mercedes-Benz", "E-Class", 5, 5000L,
                    TransmissionType.AUTOMATIC, new BigDecimal("900.00"), CarCategory.LUXURY, istanbul);

            Car car5 = createCar("CAR005", "34MNO345", "Renault", "Clio", 5, 30000L,
                    TransmissionType.MANUAL, new BigDecimal("250.00"), CarCategory.ECONOMY, istanbul);

            Car car6 = createCar("CAR006", "06PQR678", "Ford", "Transit", 12, 40000L,
                    TransmissionType.MANUAL, new BigDecimal("500.00"), CarCategory.VAN, ankara);

            Car car7 = createCar("CAR007", "07STU901", "Hyundai", "i20", 5, 8000L,
                    TransmissionType.AUTOMATIC, new BigDecimal("280.00"), CarCategory.ECONOMY, antalya);

            Car car8 = createCar("CAR008", "35VWX234", "Audi", "A6", 5, 12000L,
                    TransmissionType.AUTOMATIC, new BigDecimal("850.00"), CarCategory.LUXURY, izmir);

            log.info("Created {} cars", 8);

            // Create sample reservations
            Reservation reservation1 = createReservation(
                    member1, car1, istanbul, istanbul,
                    LocalDateTime.now().plusDays(5),
                    LocalDateTime.now().plusDays(10),
                    Arrays.asList(gps, wifi));

            Reservation reservation2 = createReservation(
                    member2, car3, izmir, ankara,
                    LocalDateTime.now().plusDays(15),
                    LocalDateTime.now().plusDays(20),
                    Arrays.asList(gps, childSeat, additionalDriver));

            Reservation reservation3 = createReservation(
                    member3, car5, istanbul, izmir,
                    LocalDateTime.now().plusDays(7),
                    LocalDateTime.now().plusDays(12),
                    Arrays.asList(wifi));

            log.info("Created {} reservations", 3);

            log.info("Data initialization completed successfully!");

        } catch (Exception e) {
            log.error("Error during data initialization: {}", e.getMessage(), e);
        }
    }

    private Location createLocation(String code, String name) {
        Location location = new Location();
        location.setCode(code);
        location.setName(name);
        return locationService.createLocation(location);
    }

    private Extra createExtra(String name, BigDecimal price) {
        Extra extra = new Extra();
        extra.setName(name);
        extra.setPrice(price);
        return extraService.createExtra(extra);
    }

    private Member createMember(String name, String address, String email, String phone, String drivingLicense) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(address);
        member.setEmail(email);
        member.setPhone(phone);
        member.setDrivingLicenseNumber(drivingLicense);
        return memberService.createMember(member);
    }

    private Car createCar(String barcode, String licensePlate, String brand, String model,
            int seats, long mileage, TransmissionType transmission,
            BigDecimal dailyPrice, CarCategory category, Location location) {
        Car car = new Car();
        car.setBarcode(barcode);
        car.setLicensePlate(licensePlate);
        car.setBrand(brand);
        car.setModel(model);
        car.setNumberOfSeats(seats);
        car.setMileage(mileage);
        car.setTransmissionType(transmission);
        car.setDailyPrice(dailyPrice);
        car.setCategory(category);
        car.setStatus(CarStatus.AVAILABLE);
        car.setLocation(location);
        return carService.createCar(car);
    }

    private Reservation createReservation(Member member, Car car, Location pickUp, Location dropOff,
            LocalDateTime pickUpDate, LocalDateTime dropOffDate,
            java.util.List<Extra> extras) {
        Reservation reservation = new Reservation();
        reservation.setMember(member);
        reservation.setCar(car);
        reservation.setPickUpLocation(pickUp);
        reservation.setDropOffLocation(dropOff);
        reservation.setPickUpDateTime(pickUpDate);
        reservation.setDropOffDateTime(dropOffDate);
        reservation.setExtras(extras);
        return reservationService.createReservation(reservation);
    }
}
