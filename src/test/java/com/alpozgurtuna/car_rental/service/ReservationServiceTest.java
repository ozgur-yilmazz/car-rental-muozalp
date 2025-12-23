package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.*;
import com.alpozgurtuna.car_rental.dto.RentedCarDTO;
import com.alpozgurtuna.car_rental.dto.ReservationDTO;
import com.alpozgurtuna.car_rental.dto.ReservationRequestDTO;
import com.alpozgurtuna.car_rental.repository.CarRepository;
import com.alpozgurtuna.car_rental.repository.ExtraRepository;
import com.alpozgurtuna.car_rental.repository.LocationRepository;
import com.alpozgurtuna.car_rental.repository.MemberRepository;
import com.alpozgurtuna.car_rental.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReservationServiceTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarService carService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ExtraRepository extraRepository;

    @Autowired
    private ReservationService reservationService;

    private Reservation testReservation;
    private ReservationRequestDTO testRequest;
    private Car testCar;
    private Member testMember;
    private Location pickUpLocation;
    private Location dropOffLocation;
    private Extra testExtra;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        reservationRepository.deleteAll();
        reservationRepository.flush();
        carRepository.deleteAll();
        carRepository.flush();
        memberRepository.deleteAll();
        memberRepository.flush();
        locationRepository.deleteAll();
        locationRepository.flush();
        extraRepository.deleteAll();
        extraRepository.flush();

        // Create and save test locations
        pickUpLocation = new Location();
        pickUpLocation.setCode("IST_TEST");
        pickUpLocation.setName("Istanbul Airport Test");
        pickUpLocation = locationRepository.save(pickUpLocation);

        dropOffLocation = new Location();
        dropOffLocation.setCode("ANK_TEST");
        dropOffLocation.setName("Ankara Airport Test");
        dropOffLocation = locationRepository.save(dropOffLocation);

        // Create and save test member
        testMember = new Member();
        testMember.setName("Ahmet Yılmaz");
        testMember.setEmail("ahmet.test@email.com");
        testMember.setPhone("5551234567");
        testMember.setAddress("Test Address");
        testMember.setDrivingLicenseNumber("TR123456789_TEST");
        testMember = memberRepository.save(testMember);

        // Create and save test car
        testCar = new Car();
        testCar.setBarcode("CAR001_TEST");
        testCar.setLicensePlate("34ABC123_TEST");
        testCar.setBrand("Toyota");
        testCar.setModel("Corolla");
        testCar.setNumberOfSeats(5);
        testCar.setMileage(15000L);
        testCar.setTransmissionType(TransmissionType.AUTOMATIC);
        testCar.setDailyPrice(new BigDecimal("350.00"));
        testCar.setCategory(CarCategory.SEDAN);
        testCar.setStatus(CarStatus.AVAILABLE);
        testCar.setLocation(pickUpLocation);
        testCar = carService.createCar(testCar);

        // Create test extra
        testExtra = new Extra();
        testExtra.setCode("GPS_TEST");
        testExtra.setName("GPS Navigation Test");
        testExtra.setPrice(new BigDecimal("50.00"));
        testExtra = extraRepository.save(testExtra);

        // Create test request
        testRequest = new ReservationRequestDTO();
        testRequest.setCarBarcode("CAR001_TEST");
        testRequest.setMemberId(testMember.getId());
        testRequest.setPickUpLocationCode("IST_TEST");
        testRequest.setDropOffLocationCode("ANK_TEST");
        testRequest.setPickUpDateTime(LocalDateTime.now().plusDays(5));
        testRequest.setDropOffDateTime(LocalDateTime.now().plusDays(10));
    }

    @Test
    void createReservation_Success() {
        ReservationDTO result = reservationService.createReservation(testRequest);

        assertNotNull(result);
        assertNotNull(result.getReservationNumber());
        assertEquals(ReservationStatus.ACTIVE, result.getStatus());
        
        // Verify car status changed to RESERVED
        Car car = carService.getCarByBarcode("CAR001_TEST");
        assertEquals(CarStatus.RESERVED, car.getStatus());
    }

    @Test
    void createReservation_CarNotAvailable_ThrowsException() {
        carService.updateCarStatus(testCar.getId(), CarStatus.RESERVED);

        assertThrows(IllegalStateException.class, () -> reservationService.createReservation(testRequest));
    }

    @Test
    void createReservation_ConflictingReservation_ThrowsException() {
        // Create first reservation
        reservationService.createReservation(testRequest);

        // Try to create conflicting reservation with overlapping dates
        ReservationRequestDTO conflictingRequest = new ReservationRequestDTO();
        conflictingRequest.setCarBarcode("CAR001_TEST");
        conflictingRequest.setMemberId(testMember.getId());
        conflictingRequest.setPickUpLocationCode("IST_TEST");
        conflictingRequest.setDropOffLocationCode("ANK_TEST");
        conflictingRequest.setPickUpDateTime(LocalDateTime.now().plusDays(6));
        conflictingRequest.setDropOffDateTime(LocalDateTime.now().plusDays(9));

        assertThrows(IllegalStateException.class, () -> reservationService.createReservation(conflictingRequest));
    }

    @Test
    void createReservation_InvalidDates_ThrowsException() {
        testRequest.setPickUpDateTime(LocalDateTime.now().plusDays(10));
        testRequest.setDropOffDateTime(LocalDateTime.now().plusDays(5)); // Drop-off before pick-up

        assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(testRequest));
    }

    @Test
    void createReservation_PastDate_ThrowsException() {
        testRequest.setPickUpDateTime(LocalDateTime.now().minusDays(1));
        testRequest.setDropOffDateTime(LocalDateTime.now().plusDays(5));

        assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(testRequest));
    }

    @Test
    void getReservationById_Success() {
        ReservationDTO created = reservationService.createReservation(testRequest);
        Reservation saved = reservationRepository.findByReservationNumber(created.getReservationNumber()).orElseThrow();

        Reservation result = reservationService.getReservationById(saved.getId());

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
    }

    @Test
    void getReservationById_NotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> reservationService.getReservationById(999L));
    }

    @Test
    void getReservationByNumber_Success() {
        ReservationDTO created = reservationService.createReservation(testRequest);

        Reservation result = reservationService.getReservationByNumber(created.getReservationNumber());

        assertNotNull(result);
        assertEquals(created.getReservationNumber(), result.getReservationNumber());
    }

    @Test
    void getAllReservations_Success() {
        reservationService.createReservation(testRequest);

        List<Reservation> result = reservationService.getAllReservations();

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void getReservationsByMember_Success() {
        reservationService.createReservation(testRequest);

        List<Reservation> result = reservationService.getReservationsByMember(testMember.getId());

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void getReservationsByCar_Success() {
        reservationService.createReservation(testRequest);

        List<Reservation> result = reservationService.getReservationsByCar(testCar.getId());

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void getReservationsByStatus_Success() {
        reservationService.createReservation(testRequest);

        List<Reservation> result = reservationService.getReservationsByStatus(ReservationStatus.ACTIVE);

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void returnCar_Success() {
        ReservationDTO created = reservationService.createReservation(testRequest);

        boolean result = reservationService.returnCar(created.getReservationNumber());

        assertTrue(result);
        
        Reservation reservation = reservationService.getReservationByNumber(created.getReservationNumber());
        assertEquals(ReservationStatus.COMPLETED, reservation.getStatus());
        assertNotNull(reservation.getReturnDateTime());
        

        Car car = carService.getCarByBarcode("CAR001_TEST");
        assertEquals(CarStatus.AVAILABLE, car.getStatus());
    }

    @Test
    void returnCar_NotFound_ReturnsFalse() {
        boolean result = reservationService.returnCar("INVALID");

        assertFalse(result);
    }

    @Test
    void cancelReservation_Success() {
        ReservationDTO created = reservationService.createReservation(testRequest);

        boolean result = reservationService.cancelReservation(created.getReservationNumber());

        assertTrue(result);
        
        Reservation reservation = reservationService.getReservationByNumber(created.getReservationNumber());
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        

        Car car = carService.getCarByBarcode("CAR001_TEST");
        assertEquals(CarStatus.AVAILABLE, car.getStatus());
    }

    @Test
    void cancelReservation_NotFound_ReturnsFalse() {
        boolean result = reservationService.cancelReservation("INVALID");

        assertFalse(result);
    }

    @Test
    void deleteReservation_Success() {
        ReservationDTO created = reservationService.createReservation(testRequest);

        boolean result = reservationService.deleteReservation(created.getReservationNumber());

        assertTrue(result);
        

        assertTrue(reservationRepository.findByReservationNumber(created.getReservationNumber()).isEmpty());
        

        Car car = carService.getCarByBarcode("CAR001_TEST");
        assertEquals(CarStatus.AVAILABLE, car.getStatus());
    }

    @Test
    void deleteReservation_NotFound_ReturnsFalse() {
        boolean result = reservationService.deleteReservation("INVALID");

        assertFalse(result);
    }

    @Test
    void addExtra_Success() {
        ReservationDTO created = reservationService.createReservation(testRequest);

        boolean result = reservationService.addExtra(created.getReservationNumber(), "GPS_TEST");

        assertTrue(result);
        
        Reservation reservation = reservationService.getReservationByNumber(created.getReservationNumber());
        assertTrue(reservation.getExtras().stream().anyMatch(e -> e.getCode().equals("GPS_TEST")));
    }

    @Test
    void addExtra_ReservationNotFound_ReturnsFalse() {
        boolean result = reservationService.addExtra("INVALID", "GPS_TEST");

        assertFalse(result);
    }

    @Test
    void addExtra_ExtraNotFound_ReturnsFalse() {
        ReservationDTO created = reservationService.createReservation(testRequest);

        boolean result = reservationService.addExtra(created.getReservationNumber(), "INVALID");

        assertFalse(result);
    }

    @Test
    void addExtra_AlreadyExists_ReturnsFalse() {
        ReservationDTO created = reservationService.createReservation(testRequest);
        reservationService.addExtra(created.getReservationNumber(), "GPS_TEST");

        boolean result = reservationService.addExtra(created.getReservationNumber(), "GPS_TEST");

        assertFalse(result);
    }

    @Test
    void getRentedCars_FutureActiveReservation_NotReturned() {
        reservationService.createReservation(testRequest); 

        List<RentedCarDTO> rented = reservationService.getRentedCars();
        assertNotNull(rented);
        assertTrue(rented.isEmpty());
    }

    @Test
    void getRentedCars_CurrentlyRented_Returned() {
        Reservation reservation = new Reservation();
        reservation.setReservationNumber("RENTED123");
        reservation.setCar(testCar);
        reservation.setMember(testMember);
        reservation.setPickUpLocation(pickUpLocation);
        reservation.setDropOffLocation(dropOffLocation);
        reservation.setCreationDate(LocalDateTime.now().minusDays(2));
        reservation.setPickUpDateTime(LocalDateTime.now().minusDays(1));
        reservation.setDropOffDateTime(LocalDateTime.now().plusDays(1));
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservationRepository.save(reservation);

        List<RentedCarDTO> rented = reservationService.getRentedCars();
        assertNotNull(rented);
        assertEquals(1, rented.size());
        assertEquals("CAR001_TEST", rented.get(0).getBarcode());
        assertEquals("RENTED123", rented.get(0).getReservationNumber());
    }
}
