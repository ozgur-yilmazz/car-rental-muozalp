package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.Extra;
import com.alpozgurtuna.car_rental.repository.ExtraRepository;
import com.alpozgurtuna.car_rental.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ExtraServiceTest {

    @Autowired
    private ExtraRepository extraRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ExtraService extraService;

    private Extra testExtra;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        reservationRepository.flush();
        extraRepository.deleteAll();
        extraRepository.flush();
        
        testExtra = new Extra();
        testExtra.setCode("GPS_TEST_CODE");
        testExtra.setName("GPS_TEST");
        testExtra.setPrice(new BigDecimal("50.00"));
    }

    @Test
    void createExtra_Success() {
        Extra result = extraService.createExtra(testExtra);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("GPS_TEST", result.getName());
        assertEquals(new BigDecimal("50.00"), result.getPrice());
    }

    @Test
    void getExtraById_Success() {
        Extra saved = extraService.createExtra(testExtra);

        Extra result = extraService.getExtraById(saved.getId());

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("GPS_TEST", result.getName());
    }

    @Test
    void getExtraById_NotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> extraService.getExtraById(999L));
    }

    @Test
    void getExtraByName_Success() {
        extraService.createExtra(testExtra);

        Extra result = extraService.getExtraByName("GPS_TEST");

        assertNotNull(result);
        assertEquals("GPS_TEST", result.getName());
    }

    @Test
    void getExtraByName_NotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> extraService.getExtraByName("Not Found"));
    }

    @Test
    void getAllExtras_Success() {
        extraService.createExtra(testExtra);

        List<Extra> result = extraService.getAllExtras();

        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertTrue(result.stream().anyMatch(e -> e.getName().equals("GPS_TEST")));
    }

    @Test
    void getExtrasByMaxPrice_Success() {
        extraService.createExtra(testExtra);

        List<Extra> result = extraService.getExtrasByMaxPrice(new BigDecimal("100.00"));

        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertTrue(result.stream().allMatch(e -> e.getPrice().compareTo(new BigDecimal("100.00")) <= 0));
    }

    @Test
    void updateExtra_Success() {
        Extra saved = extraService.createExtra(testExtra);

        Extra updatedExtra = new Extra();
        updatedExtra.setCode("GPS_UPDATED");
        updatedExtra.setName("GPS Navigation System Pro");
        updatedExtra.setPrice(new BigDecimal("75.00"));

        Extra result = extraService.updateExtra(saved.getId(), updatedExtra);

        assertNotNull(result);
        assertEquals("GPS Navigation System Pro", result.getName());
        assertEquals(new BigDecimal("75.00"), result.getPrice());
    }

    @Test
    void deleteExtra_Success() {
        Extra saved = extraService.createExtra(testExtra);

        extraService.deleteExtra(saved.getId());

        assertThrows(IllegalArgumentException.class, () -> extraService.getExtraById(saved.getId()));
    }

    @Test
    void deleteExtra_NotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> extraService.deleteExtra(999L));
    }
}
