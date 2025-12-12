package com.example.festival_system.controller;

import com.example.festival_system.model.Festival;
import com.example.festival_system.service.FestivalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/festivals")
public class FestivalRestController {

    @Autowired
    private FestivalService festivalService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public List<Festival> getAllFestivals() {
        return festivalService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Festival> getFestivalById(@PathVariable Long id) {
        Festival festival = festivalService.findById(id);
        return festival != null ? ResponseEntity.ok(festival) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Festival> addFestival(@RequestBody Festival festival) {
        Festival saved = festivalService.save(festival);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Festival> updateFestival(@PathVariable Long id, @RequestBody Festival festival) {
        Festival existing = festivalService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        festival.setId(id);
        return ResponseEntity.ok(festivalService.save(festival));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteFestival(@PathVariable Long id) {
        Festival existing = festivalService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        festivalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}