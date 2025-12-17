package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.Extra;
import com.alpozgurtuna.car_rental.repository.ExtraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtraServiceTest {

    @Mock
    private ExtraRepository extraRepository;

    @InjectMocks
    private ExtraService extraService;

    private Extra testExtra;

    @BeforeEach
    void setUp() {
        testExtra = new Extra();
        testExtra.setId(1L);
        testExtra.setName("GPS Navigation System");
        testExtra.setPrice(new BigDecimal("50.00"));
    }

    @Test
    void createExtra_Success() {
        when(extraRepository.save(any(Extra.class))).thenReturn(testExtra);

        Extra result = extraService.createExtra(testExtra);

        assertNotNull(result);
        assertEquals("GPS Navigation System", result.getName());
        assertEquals(new BigDecimal("50.00"), result.getPrice());
        verify(extraRepository, times(1)).save(testExtra);
    }

    @Test
    void getExtraById_Success() {
        when(extraRepository.findById(anyLong())).thenReturn(Optional.of(testExtra));

        Extra result = extraService.getExtraById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("GPS Navigation System", result.getName());
    }

    @Test
    void getExtraById_NotFound_ThrowsException() {
        when(extraRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> extraService.getExtraById(999L));
    }

    @Test
    void getExtraByName_Success() {
        when(extraRepository.findByName(anyString())).thenReturn(Optional.of(testExtra));

        Extra result = extraService.getExtraByName("GPS Navigation System");

        assertNotNull(result);
        assertEquals("GPS Navigation System", result.getName());
    }

    @Test
    void getExtraByName_NotFound_ThrowsException() {
        when(extraRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> extraService.getExtraByName("Not Found"));
    }

    @Test
    void getAllExtras_Success() {
        List<Extra> extras = Arrays.asList(testExtra);
        when(extraRepository.findAll()).thenReturn(extras);

        List<Extra> result = extraService.getAllExtras();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("GPS Navigation System", result.get(0).getName());
    }

    @Test
    void getExtrasByMaxPrice_Success() {
        List<Extra> extras = Arrays.asList(testExtra);
        when(extraRepository.findByPriceLessThanEqual(any(BigDecimal.class))).thenReturn(extras);

        List<Extra> result = extraService.getExtrasByMaxPrice(new BigDecimal("100.00"));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getPrice().compareTo(new BigDecimal("100.00")) <= 0);
    }

    @Test
    void updateExtra_Success() {
        Extra updatedExtra = new Extra();
        updatedExtra.setName("GPS Navigation System Pro");
        updatedExtra.setPrice(new BigDecimal("75.00"));

        when(extraRepository.findById(anyLong())).thenReturn(Optional.of(testExtra));
        when(extraRepository.save(any(Extra.class))).thenReturn(updatedExtra);

        Extra result = extraService.updateExtra(1L, updatedExtra);

        assertNotNull(result);
        verify(extraRepository, times(1)).save(any(Extra.class));
    }

    @Test
    void deleteExtra_Success() {
        when(extraRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(extraRepository).deleteById(anyLong());

        extraService.deleteExtra(1L);

        verify(extraRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteExtra_NotFound_ThrowsException() {
        when(extraRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> extraService.deleteExtra(999L));
        verify(extraRepository, never()).deleteById(anyLong());
    }
}
