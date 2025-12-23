package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.Member;
import com.alpozgurtuna.car_rental.repository.MemberRepository;
import com.alpozgurtuna.car_rental.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberService memberService;

    private Member testMember;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        reservationRepository.flush();
        memberRepository.deleteAll();
        memberRepository.flush();
        
        testMember = new Member();
        testMember.setName("Ahmet Yılmaz");
        testMember.setAddress("Istanbul, Turkey");
        testMember.setEmail("ahmet.yilmaz.test@email.com");
        testMember.setPhone("+90-532-111-2233");
        testMember.setDrivingLicenseNumber("TR123456789_TEST");
    }

    @Test
    void createMember_Success() {
        Member result = memberService.createMember(testMember);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Ahmet Yılmaz", result.getName());
        assertEquals("ahmet.yilmaz.test@email.com", result.getEmail());
    }

    @Test
    void createMember_DuplicateEmail_ThrowsException() {
        memberService.createMember(testMember);

        Member duplicate = new Member();
        duplicate.setName("Different Name");
        duplicate.setEmail("ahmet.yilmaz.test@email.com");
        duplicate.setPhone("+90-533-222-3344");
        duplicate.setAddress("Different Address");
        duplicate.setDrivingLicenseNumber("TR987654321");

        assertThrows(IllegalArgumentException.class, () -> memberService.createMember(duplicate));
    }

    @Test
    void createMember_DuplicateDrivingLicense_ThrowsException() {
        memberService.createMember(testMember);

        Member duplicate = new Member();
        duplicate.setName("Different Name");
        duplicate.setEmail("different@email.com");
        duplicate.setPhone("+90-533-222-3344");
        duplicate.setAddress("Different Address");
        duplicate.setDrivingLicenseNumber("TR123456789_TEST");

        assertThrows(IllegalArgumentException.class, () -> memberService.createMember(duplicate));
    }

    @Test
    void getMemberById_Success() {
        Member saved = memberService.createMember(testMember);

        Member result = memberService.getMemberById(saved.getId());

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Ahmet Yılmaz", result.getName());
    }

    @Test
    void getMemberById_NotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> memberService.getMemberById(999L));
    }

    @Test
    void getMemberByEmail_Success() {
        memberService.createMember(testMember);

        Member result = memberService.getMemberByEmail("ahmet.yilmaz.test@email.com");

        assertNotNull(result);
        assertEquals("ahmet.yilmaz.test@email.com", result.getEmail());
    }

    @Test
    void getMemberByEmail_NotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> memberService.getMemberByEmail("notfound@email.com"));
    }

    @Test
    void getMemberByDrivingLicense_Success() {
        memberService.createMember(testMember);

        Member result = memberService.getMemberByDrivingLicense("TR123456789_TEST");

        assertNotNull(result);
        assertEquals("TR123456789_TEST", result.getDrivingLicenseNumber());
    }

    @Test
    void getAllMembers_Success() {
        memberService.createMember(testMember);

        List<Member> result = memberService.getAllMembers();

        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertTrue(result.stream().anyMatch(m -> m.getName().equals("Ahmet Yılmaz")));
    }

    @Test
    void updateMember_Success() {
        Member saved = memberService.createMember(testMember);

        Member updatedMember = new Member();
        updatedMember.setName("Ahmet Yılmaz Updated");
        updatedMember.setAddress("Ankara, Turkey");
        updatedMember.setEmail("ahmet.yilmaz.test@email.com");
        updatedMember.setPhone("+90-532-999-8888");
        updatedMember.setDrivingLicenseNumber("TR123456789_TEST");

        Member result = memberService.updateMember(saved.getId(), updatedMember);

        assertNotNull(result);
        assertEquals("Ahmet Yılmaz Updated", result.getName());
        assertEquals("Ankara, Turkey", result.getAddress());
    }

    @Test
    void updateMember_DuplicateEmail_ThrowsException() {
        Member saved = memberService.createMember(testMember);
        
        Member other = new Member();
        other.setName("Other Member");
        other.setEmail("other@email.com");
        other.setPhone("+90-533-222-3344");
        other.setAddress("Other Address");
        other.setDrivingLicenseNumber("TR987654321");
        memberService.createMember(other);

        Member updatedMember = new Member();
        updatedMember.setName("Ahmet Yılmaz");
        updatedMember.setEmail("other@email.com"); // Try to use other's email
        updatedMember.setPhone("+90-532-111-2233");
        updatedMember.setAddress("Istanbul, Turkey");
        updatedMember.setDrivingLicenseNumber("TR123456789_TEST");

        assertThrows(IllegalArgumentException.class,
                () -> memberService.updateMember(saved.getId(), updatedMember));
    }

    @Test
    void deleteMember_Success() {
        Member saved = memberService.createMember(testMember);

        memberService.deleteMember(saved.getId());

        assertThrows(IllegalArgumentException.class, () -> memberService.getMemberById(saved.getId()));
    }

    @Test
    void deleteMember_NotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> memberService.deleteMember(999L));
    }
}
