package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.Member;
import com.alpozgurtuna.car_rental.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public Member createMember(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new IllegalArgumentException("Member with email " + member.getEmail() + " already exists");
        }
        if (memberRepository.existsByDrivingLicenseNumber(member.getDrivingLicenseNumber())) {
            throw new IllegalArgumentException(
                    "Member with driving license " + member.getDrivingLicenseNumber() + " already exists");
        }
        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public Member getMemberByDrivingLicense(String drivingLicenseNumber) {
        return memberRepository.findByDrivingLicenseNumber(drivingLicenseNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found with driving license: " + drivingLicenseNumber));
    }

    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member updateMember(Long id, Member memberDetails) {
        Member member = getMemberById(id);

        if (!member.getEmail().equals(memberDetails.getEmail()) &&
                memberRepository.existsByEmail(memberDetails.getEmail())) {
            throw new IllegalArgumentException("Member with email " + memberDetails.getEmail() + " already exists");
        }

        if (!member.getDrivingLicenseNumber().equals(memberDetails.getDrivingLicenseNumber()) &&
                memberRepository.existsByDrivingLicenseNumber(memberDetails.getDrivingLicenseNumber())) {
            throw new IllegalArgumentException(
                    "Member with driving license " + memberDetails.getDrivingLicenseNumber() + " already exists");
        }

        member.setName(memberDetails.getName());
        member.setAddress(memberDetails.getAddress());
        member.setEmail(memberDetails.getEmail());
        member.setPhone(memberDetails.getPhone());
        member.setDrivingLicenseNumber(memberDetails.getDrivingLicenseNumber());

        return memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }
}
