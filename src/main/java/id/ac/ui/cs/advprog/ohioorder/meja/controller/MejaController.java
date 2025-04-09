package id.ac.ui.cs.advprog.ohioorder.meja.controller;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaRequest;
import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.service.MejaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/meja")
@RequiredArgsConstructor
public class MejaController {
    
    private final MejaService mejaService;
    
    @PostMapping
    public ResponseEntity<MejaResponse> createMeja(@Valid @RequestBody MejaRequest request) {
        return new ResponseEntity<>(mejaService.createMeja(request), HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<MejaResponse>> getAllMeja() {
        return ResponseEntity.ok(mejaService.getAllMeja());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MejaResponse> getMejaById(@PathVariable UUID id) {
        return ResponseEntity.ok(mejaService.getMejaById(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MejaResponse> updateMeja(@PathVariable UUID id, @Valid @RequestBody MejaRequest request) {
        return ResponseEntity.ok(mejaService.updateMeja(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeja(@PathVariable UUID id) {
        mejaService.deleteMeja(id);
        return ResponseEntity.noContent().build();
    }
}