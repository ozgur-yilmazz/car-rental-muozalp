package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.*;
import com.alpozgurtuna.car_rental.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CarService carService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation testReservation;
    private Car testCar;
    private Member testMember;
    private Location pickUpLocation;
    private Location dropOffLocation;

    @BeforeEach
    void setUp() {
        pickUpLocation = new Location(1L, "IST", "Istanbul Airport");
        dropOffLocation = new Location(2L, "ANK", "Ankara Airport");

        testMember = new Member();
        testMember.setId(1L);
        testMember.setName("Ahmet Yılmaz");
        testMember.setEmail("ahmet@email.com");
        testMember.setDrivingLicenseNumber("TR123456789");

        testCar = new Car();
        testCar.setId(1L);
        testCar.setBarcode("CAR001");
        testCar.setLicensePlate("34ABC123");
        testCar.setBrand("Toyota");
        testCar.setModel("Corolla");
        testCar.setDailyPrice(new BigDecimal("350.00"));
        testCar.setStatus(CarStatus.AVAILABLE);
        testCar.setLocation(pickUpLocation);

        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setReservationNumber("RES-12345678");
        testReservation.setCreationDate(LocalDateTime.now());
        testReservation.setPickUpDateTime(LocalDateTime.now().plusDays(5));
        testReservation.setDropOffDateTime(LocalDateTime.now().plusDays(10));
        testReservation.setStatus(ReservationStatus.ACTIVE);
        testReservation.setMember(testMember);
        testReservation.setCar(testCar);
        testReservation.setPickUpLocation(pickUpLocation);
        testReservation.setDropOffLocation(dropOffLocation);
        testReservation.setExtras(new ArrayList<>());
    }

    @Test
    void createReservation_Success() {
        when(carService.getCarById(anyLong())).thenReturn(testCar);
        when(reservationRepository.findConflictingReservations(anyLong(), any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        doNothing().when(carService).updateCarStatus(anyLong(), any(CarStatus.class));

        Reservation result = reservationService.createReservation(testReservation);

        assertNotNull(result);
        assertNotNull(result.getReservationNumber());
        assertEquals(ReservationStatus.ACTIVE, result.getStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(carService, times(1)).updateCarStatus(testCar.getId(), CarStatus.RESERVED);
    }

    @Test
    void createReservation_CarNotAvailable_ThrowsException() {
        testCar.setStatus(CarStatus.RESERVED);
        when(carService.getCarById(anyLong())).thenReturn(testCar);

        assertThrows(IllegalStateException.class,
                () -> reservationService.createReservation(testReservation));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void createReservation_ConflictingReservation_ThrowsException() {
        when(carService.getCarById(anyLong())).thenReturn(testCar);
        when(reservationRepository.findConflictingReservations(anyLong(), any(), any(), any()))
                .thenReturn(Arrays.asList(testReservation));

        assertThrows(IllegalStateException.class,
                () -> reservationService.createReservation(testReservation));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void createReservation_InvalidDates_ThrowsException() {
        testReservation.setDropOffDateTime(LocalDateTime.now().plusDays(3));
        when(carService.getCarById(anyLong())).thenReturn(testCar);
        when(reservationRepository.findConflictingReservations(anyLong(), any(), any(), any()))
                .thenReturn(new ArrayList<>());

        assertThrows(IllegalArgumentException.class,
                () -> reservationService.createReservation(testReservation));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void createReservation_PastDate_ThrowsException() {
        testReservation.setPickUpDateTime(LocalDateTime.now().minusDays(1));
        testReservation.setDropOffDateTime(LocalDateTime.now().plusDays(5));
        when(carService.getCarById(anyLong())).thenReturn(testCar);
        when(reservationRepository.findConflictingReservations(anyLong(), any(), any(), any()))
                .thenReturn(new ArrayList<>());

        assertThrows(IllegalArgumentException.class,
                () -> reservationService.createReservation(testReservation));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void getReservationById_Success() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(testReservation));

        Reservation result = reservationService.getReservationById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getReservationById_NotFound_ThrowsException() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> reservationService.getReservationById(999L));
    }

    @Test
    void getReservationByNumber_Success() {
        when(reservationRepository.findByReservationNumber(anyString()))
                .thenReturn(Optional.of(testReservation));

        Reservation result = reservationService.getReservationByNumber("RES-12345678");

        assertNotNull(result);
        assertEquals("RES-12345678", result.getReservationNumber());
    }

    @Test
    void getAllReservations_Success() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<Reservation> result = reservationService.getAllReservations();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getReservationsByMember_Success() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByMemberId(anyLong())).thenReturn(reservations);

        List<Reservation> result = reservationService.getReservationsByMember(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getReservationsByCar_Success() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByCarId(anyLong())).thenReturn(reservations);

        List<Reservation> result = reservationService.getReservationsByCar(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getReservationsByStatus_Success() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByStatus(any(ReservationStatus.class)))
                .thenReturn(reservations);

        List<Reservation> result = reservationService.getReservationsByStatus(ReservationStatus.ACTIVE);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void completeReservation_Success() {
        LocalDateTime returnDate = LocalDateTime.now();
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        when(carService.updateCar(anyLong(), any(Car.class))).thenReturn(testCar);

        Reservation result = reservationService.completeReservation(1L, returnDate);

        assertNotNull(result);
        assertEquals(ReservationStatus.COMPLETED, testReservation.getStatus());
        assertEquals(returnDate, testReservation.getReturnDateTime());
        verify(carService, times(1)).updateCar(anyLong(), any(Car.class));
    }

    @Test
    void completeReservation_NotActive_ThrowsException() {
        testReservation.setStatus(ReservationStatus.CANCELLED);
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(testReservation));

        assertThrows(IllegalStateException.class,
                () -> reservationService.completeReservation(1L, LocalDateTime.now()));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void cancelReservation_Success() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        doNothing().when(carService).updateCarStatus(anyLong(), any(CarStatus.class));

        Reservation result = reservationService.cancelReservation(1L);

        assertNotNull(result);
        assertEquals(ReservationStatus.CANCELLED, testReservation.getStatus());
        verify(carService, times(1)).updateCarStatus(testCar.getId(), CarStatus.AVAILABLE);
    }

    @Test
    void cancelReservation_AlreadyCompleted_ThrowsException() {
        testReservation.setStatus(ReservationStatus.COMPLETED);
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(testReservation));

        assertThrows(IllegalStateException.class,
                () -> reservationService.cancelReservation(1L));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void cancelReservation_AlreadyCancelled_ThrowsException() {
        testReservation.setStatus(ReservationStatus.CANCELLED);
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(testReservation));

        assertThrows(IllegalStateException.class,
                () -> reservationService.cancelReservation(1L));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void deleteReservation_Success() {
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(testReservation));
        doNothing().when(carService).updateCarStatus(anyLong(), any(CarStatus.class));
        doNothing().when(reservationRepository).deleteById(anyLong());

        reservationService.deleteReservation(1L);

        verify(carService, times(1)).updateCarStatus(testCar.getId(), CarStatus.AVAILABLE);
        verify(reservationRepository, times(1)).deleteById(1L);
    }
}
