package com.alpozgurtuna.car_rental.service;

import com.alpozgurtuna.car_rental.domain.Extra;
import com.alpozgurtuna.car_rental.repository.ExtraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtraService {

    private final ExtraRepository extraRepository;

    public Extra createExtra(Extra extra) {
        return extraRepository.save(extra);
    }

    @Transactional(readOnly = true)
    public Extra getExtraById(Long id) {
        return extraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Extra not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Extra getExtraByName(String name) {
        return extraRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Extra not found with name: " + name));
    }

    @Transactional(readOnly = true)
    public List<Extra> getAllExtras() {
        return extraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Extra> getExtrasByMaxPrice(BigDecimal maxPrice) {
        return extraRepository.findByPriceLessThanEqual(maxPrice);
    }

    public Extra updateExtra(Long id, Extra extraDetails) {
        Extra extra = getExtraById(id);

        extra.setName(extraDetails.getName());
        extra.setPrice(extraDetails.getPrice());

        return extraRepository.save(extra);
    }

    public void deleteExtra(Long id) {
        if (!extraRepository.existsById(id)) {
            throw new IllegalArgumentException("Extra not found with id: " + id);
        }
        extraRepository.deleteById(id);
    }
}
