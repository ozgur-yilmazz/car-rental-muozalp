package com.alpozgurtuna.car_rental.controller;

import com.alpozgurtuna.car_rental.domain.Extra;
import com.alpozgurtuna.car_rental.dto.DtoMapper;
import com.alpozgurtuna.car_rental.dto.ExtraDTO;
import com.alpozgurtuna.car_rental.service.ExtraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/extras")
@RequiredArgsConstructor
public class ExtraController {

    private final ExtraService extraService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<ExtraDTO>> getAllExtras() {
        List<Extra> extras = extraService.getAllExtras();
        return ResponseEntity.ok(extras.stream()
                .map(dtoMapper::toExtraDTO)
                .collect(Collectors.toList()));
    }
}
