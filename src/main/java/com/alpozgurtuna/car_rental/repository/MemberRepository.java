package com.alpozgurtuna.car_rental.repository;

import com.alpozgurtuna.car_rental.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByDrivingLicenseNumber(String drivingLicenseNumber);

    boolean existsByEmail(String email);

    boolean existsByDrivingLicenseNumber(String drivingLicenseNumber);
}
