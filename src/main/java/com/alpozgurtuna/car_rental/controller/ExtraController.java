package com.alpozgurtuna.car_rental.controller;

import com.alpozgurtuna.car_rental.dto.ExtraDTO;
import com.alpozgurtuna.car_rental.service.ExtraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/extras")
@RequiredArgsConstructor
public class ExtraController {

    private final ExtraService extraService;

    @GetMapping
    public ResponseEntity<List<ExtraDTO>> getAllExtras() {
        return ResponseEntity.ok(extraService.getAllExtraDTOs());
    }
}
