package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.Member;
import com.alpozgurtuna.car_rental.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setId(1L);
        testMember.setName("Ahmet Yılmaz");
        testMember.setAddress("Istanbul, Turkey");
        testMember.setEmail("ahmet.yilmaz@email.com");
        testMember.setPhone("+90-532-111-2233");
        testMember.setDrivingLicenseNumber("TR123456789");
    }

    @Test
    void createMember_Success() {
        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberRepository.existsByDrivingLicenseNumber(anyString())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        Member result = memberService.createMember(testMember);

        assertNotNull(result);
        assertEquals("Ahmet Yılmaz", result.getName());
        assertEquals("ahmet.yilmaz@email.com", result.getEmail());
        verify(memberRepository, times(1)).save(testMember);
    }

    @Test
    void createMember_DuplicateEmail_ThrowsException() {
        when(memberRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> memberService.createMember(testMember));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void createMember_DuplicateDrivingLicense_ThrowsException() {
        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberRepository.existsByDrivingLicenseNumber(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> memberService.createMember(testMember));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void getMemberById_Success() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));

        Member result = memberService.getMemberById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ahmet Yılmaz", result.getName());
    }

    @Test
    void getMemberById_NotFound_ThrowsException() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> memberService.getMemberById(999L));
    }

    @Test
    void getMemberByEmail_Success() {
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(testMember));

        Member result = memberService.getMemberByEmail("ahmet.yilmaz@email.com");

        assertNotNull(result);
        assertEquals("ahmet.yilmaz@email.com", result.getEmail());
    }

    @Test
    void getMemberByEmail_NotFound_ThrowsException() {
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> memberService.getMemberByEmail("notfound@email.com"));
    }

    @Test
    void getMemberByDrivingLicense_Success() {
        when(memberRepository.findByDrivingLicenseNumber(anyString())).thenReturn(Optional.of(testMember));

        Member result = memberService.getMemberByDrivingLicense("TR123456789");

        assertNotNull(result);
        assertEquals("TR123456789", result.getDrivingLicenseNumber());
    }

    @Test
    void getAllMembers_Success() {
        List<Member> members = Arrays.asList(testMember);
        when(memberRepository.findAll()).thenReturn(members);

        List<Member> result = memberService.getAllMembers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ahmet Yılmaz", result.get(0).getName());
    }

    @Test
    void updateMember_Success() {
        Member updatedMember = new Member();
        updatedMember.setName("Ahmet Yılmaz Updated");
        updatedMember.setAddress("Ankara, Turkey");
        updatedMember.setEmail("ahmet.yilmaz@email.com");
        updatedMember.setPhone("+90-532-999-8888");
        updatedMember.setDrivingLicenseNumber("TR123456789");

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(updatedMember);

        Member result = memberService.updateMember(1L, updatedMember);

        assertNotNull(result);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void updateMember_DuplicateEmail_ThrowsException() {
        Member updatedMember = new Member();
        updatedMember.setEmail("different@email.com");
        updatedMember.setDrivingLicenseNumber("TR123456789");

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        when(memberRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> memberService.updateMember(1L, updatedMember));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void deleteMember_Success() {
        when(memberRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(memberRepository).deleteById(anyLong());

        memberService.deleteMember(1L);

        verify(memberRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMember_NotFound_ThrowsException() {
        when(memberRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> memberService.deleteMember(999L));
        verify(memberRepository, never()).deleteById(anyLong());
    }
}
