package id.ac.ui.cs.advprog.ohioorder.meja.service;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaRequest;
import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.dto.TableSessionResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MejaService {
    MejaResponse createMeja(MejaRequest request);
    List<MejaResponse> getAllMeja();
    MejaResponse getMejaById(UUID id);
    MejaResponse updateMeja(UUID id, MejaRequest request);
    void deleteMeja(UUID id);

    MejaResponse getMejaByNomorMeja(String nomorMeja);
    MejaResponse setMejaStatus(UUID id, MejaStatus status);
    boolean isMejaAvailable(UUID id);
    List<MejaResponse> getAvailableMeja();
    CompletableFuture<TableSessionResponse> createTableSession(UUID id);
    CompletableFuture<TableSessionResponse> deactivateTableSession(String sessionId);
}