package com.alpozgurtuna.car_rental.controller;

import com.alpozgurtuna.car_rental.domain.Member;
import com.alpozgurtuna.car_rental.dto.DtoMapper;
import com.alpozgurtuna.car_rental.dto.MemberDTO;
import com.alpozgurtuna.car_rental.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(members.stream()
                .map(dtoMapper::toMemberDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        try {
            Member member = memberService.getMemberById(id);
            return ResponseEntity.ok(dtoMapper.toMemberDTO(member));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@RequestBody MemberDTO memberDTO) {
        try {
            Member member = new Member();
            member.setName(memberDTO.getName());
            member.setAddress(memberDTO.getAddress());
            member.setEmail(memberDTO.getEmail());
            member.setPhone(memberDTO.getPhone());
            member.setDrivingLicenseNumber(memberDTO.getDrivingLicenseNumber());
            
            Member created = memberService.createMember(member);
            return ResponseEntity.ok(dtoMapper.toMemberDTO(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
