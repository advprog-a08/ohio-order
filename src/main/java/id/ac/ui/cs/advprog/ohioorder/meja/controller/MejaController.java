package id.ac.ui.cs.advprog.ohioorder.meja.controller;

import id.ac.ui.cs.advprog.ohioorder.annotation.AuthenticatedTableSession;
import id.ac.ui.cs.advprog.ohioorder.annotation.RequireAdmin;
import id.ac.ui.cs.advprog.ohioorder.annotation.RequireTableSession;
import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaRequest;
import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.service.MejaService;
import id.ac.ui.cs.advprog.ohioorder.meja.dto.TableSessionResponse;
import id.ac.ui.cs.advprog.ohioorder.model.TableSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/meja")
@RequiredArgsConstructor
public class MejaController {

    private final MejaService mejaService;

    @PostMapping
    @RequireAdmin
    public ResponseEntity<MejaResponse> createMeja(@Valid @RequestBody MejaRequest request) {
        return new ResponseEntity<>(mejaService.createMeja(request), HttpStatus.CREATED);
    }

    @GetMapping
    @RequireAdmin
    public ResponseEntity<List<MejaResponse>> getAllMeja() {
        return ResponseEntity.ok(mejaService.getAllMeja());
    }

    @GetMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<MejaResponse> getMejaById(@PathVariable UUID id) {
        return ResponseEntity.ok(mejaService.getMejaById(id));
    }

    @PutMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<MejaResponse> updateMeja(@PathVariable UUID id, @Valid @RequestBody MejaRequest request) {
        return ResponseEntity.ok(mejaService.updateMeja(id, request));
    }

    @DeleteMapping("/{id}")
    @RequireAdmin
    public ResponseEntity<Void> deleteMeja(@PathVariable UUID id) {
        mejaService.deleteMeja(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nomor/{nomorMeja}")
    public ResponseEntity<MejaResponse> getMejaByNomorMeja(@PathVariable String nomorMeja) {
        return ResponseEntity.ok(mejaService.getMejaByNomorMeja(nomorMeja));
    }

    @PatchMapping("/{id}/status")
    @RequireAdmin
    public ResponseEntity<MejaResponse> setMejaStatus(@PathVariable UUID id, @RequestParam MejaStatus status) {
        return ResponseEntity.ok(mejaService.setMejaStatus(id, status));
    }

    @GetMapping("/{id}/available")
    @RequireAdmin
    public ResponseEntity<Boolean> isMejaAvailable(@PathVariable UUID id) {
        return ResponseEntity.ok(mejaService.isMejaAvailable(id));
    }

    @GetMapping("/available")
    @RequireAdmin
    public ResponseEntity<List<MejaResponse>> getAvailableMeja() {
        return ResponseEntity.ok(mejaService.getAvailableMeja());
    }

    @GetMapping("/{id}/status")
    @RequireAdmin
    public ResponseEntity<MejaStatus> getMejaStatus(@PathVariable UUID id) {
        MejaResponse meja = mejaService.getMejaById(id);
        return ResponseEntity.ok(meja.getStatus());
    }

    @PostMapping("/{id}/session")
    public CompletableFuture<ResponseEntity<TableSessionResponse>> createTableSession(@PathVariable UUID id) {
        return mejaService.createTableSession(id)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/session/deactivate")
    @RequireTableSession
    public CompletableFuture<ResponseEntity<TableSessionResponse>> deactivateTableSession(@AuthenticatedTableSession TableSession tableSession) {
        return mejaService.deactivateTableSession(tableSession.getId())
                .thenApply(ResponseEntity::ok);
    }
}