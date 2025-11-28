package com.alpozgurtuna.car_rental.controller;

import com.alpozgurtuna.car_rental.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final LocationService locationService;
    private final CarService carService;
    private final MemberService memberService;
    private final ExtraService extraService;
    private final ReservationService reservationService;

    @GetMapping("/database")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = new HashMap<>();

        result.put("locations", locationService.getAllLocations());
        result.put("cars", carService.getAllCars());
        result.put("members", memberService.getAllMembers());
        result.put("extras", extraService.getAllExtras());
        result.put("reservations", reservationService.getAllReservations());

        return result;
    }

    @GetMapping("/tables")
    public Map<String, Long> getTables() {
        Map<String, Long> result = new HashMap<>();

        result.put("locations_count", locationService.getAllLocations().stream().count());
        result.put("cars_count", carService.getAllCars().stream().count());
        result.put("members_count", memberService.getAllMembers().stream().count());
        result.put("extras_count", extraService.getAllExtras().stream().count());
        result.put("reservations_count", reservationService.getAllReservations().stream().count());

        return result;
    }
}
