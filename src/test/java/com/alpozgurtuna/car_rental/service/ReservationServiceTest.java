package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.*;
import com.alpozgurtuna.car_rental.dto.DtoMapper;
import com.alpozgurtuna.car_rental.dto.ReservationDTO;
import com.alpozgurtuna.car_rental.dto.ReservationRequestDTO;
import com.alpozgurtuna.car_rental.repository.ExtraRepository;
import com.alpozgurtuna.car_rental.repository.LocationRepository;
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

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private ExtraRepository extraRepository;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation testReservation;
    private ReservationRequestDTO testRequest;
    private ReservationDTO testReservationDTO;
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

        testRequest = new ReservationRequestDTO();
        testRequest.setCarBarcode("CAR001");
        testRequest.setMemberId(1L);
        testRequest.setPickUpLocationCode("IST");
        testRequest.setDropOffLocationCode("ANK");
        testRequest.setPickUpDateTime(LocalDateTime.now().plusDays(5));
        testRequest.setDropOffDateTime(LocalDateTime.now().plusDays(10));

        testReservationDTO = new ReservationDTO();
        testReservationDTO.setReservationNumber("RES-12345678");
        testReservationDTO.setStatus(ReservationStatus.ACTIVE);
    }

    @Test
    void createReservation_Success() {
        when(carService.getCarByBarcode(anyString())).thenReturn(testCar);
        when(memberService.getMemberById(anyLong())).thenReturn(testMember);
        when(locationRepository.findByCode("IST")).thenReturn(Optional.of(pickUpLocation));
        when(locationRepository.findByCode("ANK")).thenReturn(Optional.of(dropOffLocation));
        when(reservationRepository.findConflictingReservations(anyLong(), any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        when(dtoMapper.toReservationDTO(any(Reservation.class))).thenReturn(testReservationDTO);
        doNothing().when(carService).updateCarStatus(anyLong(), any(CarStatus.class));

        ReservationDTO result = reservationService.createReservation(testRequest);

        assertNotNull(result);
        assertNotNull(result.getReservationNumber());
        assertEquals(ReservationStatus.ACTIVE, result.getStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(carService, times(1)).updateCarStatus(testCar.getId(), CarStatus.RESERVED);
    }

    @Test
    void createReservation_CarNotAvailable_ThrowsException() {
        testCar.setStatus(CarStatus.RESERVED);
        when(carService.getCarByBarcode(anyString())).thenReturn(testCar);
        when(memberService.getMemberById(anyLong())).thenReturn(testMember);
        when(locationRepository.findByCode("IST")).thenReturn(Optional.of(pickUpLocation));
        when(locationRepository.findByCode("ANK")).thenReturn(Optional.of(dropOffLocation));

        assertThrows(IllegalStateException.class, () -> reservationService.createReservation(testRequest));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void createReservation_ConflictingReservation_ThrowsException() {
        when(carService.getCarByBarcode(anyString())).thenReturn(testCar);
        when(memberService.getMemberById(anyLong())).thenReturn(testMember);
        when(locationRepository.findByCode("IST")).thenReturn(Optional.of(pickUpLocation));
        when(locationRepository.findByCode("ANK")).thenReturn(Optional.of(dropOffLocation));
        when(reservationRepository.findConflictingReservations(anyLong(), any(), any(), any()))
                .thenReturn(Arrays.asList(testReservation));

        assertThrows(IllegalStateException.class, () -> reservationService.createReservation(testRequest));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void createReservation_InvalidDates_ThrowsException() {
        testRequest.setDropOffDateTime(LocalDateTime.now().plusDays(3)); // Drop-off before pick-up (pick-up is +5 days)
        when(carService.getCarByBarcode(anyString())).thenReturn(testCar);
        when(memberService.getMemberById(anyLong())).thenReturn(testMember);
        when(locationRepository.findByCode("IST")).thenReturn(Optional.of(pickUpLocation));
        when(locationRepository.findByCode("ANK")).thenReturn(Optional.of(dropOffLocation));
        when(reservationRepository.findConflictingReservations(anyLong(), any(), any(), any()))
                .thenReturn(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(testRequest));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void createReservation_PastDate_ThrowsException() {
        testRequest.setPickUpDateTime(LocalDateTime.now().minusDays(1));
        when(carService.getCarByBarcode(anyString())).thenReturn(testCar);
        when(memberService.getMemberById(anyLong())).thenReturn(testMember);
        when(locationRepository.findByCode("IST")).thenReturn(Optional.of(pickUpLocation));
        when(locationRepository.findByCode("ANK")).thenReturn(Optional.of(dropOffLocation));
        when(reservationRepository.findConflictingReservations(anyLong(), any(), any(), any()))
                .thenReturn(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(testRequest));
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

        assertThrows(IllegalArgumentException.class, () -> reservationService.getReservationById(999L));
    }

    @Test
    void getReservationByNumber_Success() {
        when(reservationRepository.findByReservationNumber(anyString())).thenReturn(Optional.of(testReservation));

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
        when(reservationRepository.findByStatus(any(ReservationStatus.class))).thenReturn(reservations);

        List<Reservation> result = reservationService.getReservationsByStatus(ReservationStatus.ACTIVE);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void returnCar_Success() {
        when(reservationRepository.findByReservationNumber(anyString())).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        doNothing().when(carService).updateCarLocation(anyLong(), any(Location.class));
        doNothing().when(carService).updateCarStatus(anyLong(), any(CarStatus.class));

        boolean result = reservationService.returnCar("RES-12345678");

        assertTrue(result);
        assertEquals(ReservationStatus.COMPLETED, testReservation.getStatus());
        assertNotNull(testReservation.getReturnDateTime());
        verify(carService, times(1)).updateCarStatus(testCar.getId(), CarStatus.AVAILABLE);
    }

    @Test
    void returnCar_NotFound_ReturnsFalse() {
        when(reservationRepository.findByReservationNumber(anyString())).thenReturn(Optional.empty());

        boolean result = reservationService.returnCar("INVALID");

        assertFalse(result);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void cancelReservation_Success() {
        when(reservationRepository.findByReservationNumber(anyString())).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        doNothing().when(carService).updateCarStatus(anyLong(), any(CarStatus.class));

        boolean result = reservationService.cancelReservation("RES-12345678");

        assertTrue(result);
        assertEquals(ReservationStatus.CANCELLED, testReservation.getStatus());
        verify(carService, times(1)).updateCarStatus(testCar.getId(), CarStatus.AVAILABLE);
    }

    @Test
    void cancelReservation_NotFound_ReturnsFalse() {
        when(reservationRepository.findByReservationNumber(anyString())).thenReturn(Optional.empty());

        boolean result = reservationService.cancelReservation("INVALID");

        assertFalse(result);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void deleteReservation_Success() {
        when(reservationRepository.findByReservationNumber(anyString())).thenReturn(Optional.of(testReservation));
        doNothing().when(carService).updateCarStatus(anyLong(), any(CarStatus.class));
        doNothing().when(reservationRepository).delete(any(Reservation.class));

        boolean result = reservationService.deleteReservation("RES-12345678");

        assertTrue(result);
        verify(carService, times(1)).updateCarStatus(testCar.getId(), CarStatus.AVAILABLE);
        verify(reservationRepository, times(1)).delete(testReservation);
    }

    @Test
    void deleteReservation_NotFound_ReturnsFalse() {
        when(reservationRepository.findByReservationNumber(anyString())).thenReturn(Optional.empty());

        boolean result = reservationService.deleteReservation("INVALID");

        assertFalse(result);
        verify(reservationRepository, never()).delete(any(Reservation.class));
    }
}
