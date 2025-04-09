package id.ac.ui.cs.advprog.ohioorder.meja.service;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaRequest;
import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaAlreadyExistsException;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaHasPesananException;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaNotFoundException;
import id.ac.ui.cs.advprog.ohioorder.meja.factory.MejaResponseFactory;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import id.ac.ui.cs.advprog.ohioorder.meja.repository.MejaRepository;
import id.ac.ui.cs.advprog.ohioorder.meja.service.MejaService;
import id.ac.ui.cs.advprog.ohioorder.meja.validation.MejaRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MejaServiceImpl implements MejaService {

    private final MejaRepository mejaRepository;
    private final MejaRequestValidator validator;
    private final MejaResponseFactory responseFactory;

    @Override
    public MejaResponse createMeja(MejaRequest request) {
        validator.validate(request);

        Meja meja = Meja.builder()
                .nomorMeja(request.getNomorMeja())
                .status(MejaStatus.TERSEDIA)
                .build();

        meja = mejaRepository.save(meja);

        return mapToMejaResponse(meja);
    }

    @Override
    public List<MejaResponse> getAllMeja() {
        return mejaRepository.findAll()
                .stream()
                .map(this::mapToMejaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MejaResponse getMejaById(UUID id) {
        Meja meja = mejaRepository.findById(id)
                .orElseThrow(() -> new MejaNotFoundException("Meja dengan ID " + id + " tidak ditemukan"));
        
        return mapToMejaResponse(meja);
    }

    @Override
    public MejaResponse updateMeja(UUID id, MejaRequest request) {
        Meja meja = mejaRepository.findById(id)
                .orElseThrow(() -> new MejaNotFoundException("Meja dengan ID " + id + " tidak ditemukan"));
        
        validator.validateForUpdate(request, meja.getNomorMeja());
        
        meja.setNomorMeja(request.getNomorMeja());
        meja = mejaRepository.save(meja);
        
        return mapToMejaResponse(meja);
    }

    @Override
    public void deleteMeja(UUID id) {
        Meja meja = mejaRepository.findById(id)
                .orElseThrow(() -> new MejaNotFoundException("Meja dengan ID " + id + " tidak ditemukan"));

        if (meja.getStatus() == MejaStatus.TERISI) {
            throw new MejaHasPesananException("Meja tidak dapat dihapus karena sedang memiliki pesanan aktif");
        }
        
        mejaRepository.delete(meja);
    }

    private MejaResponse mapToMejaResponse(Meja meja) {
        return responseFactory.createFromEntity(meja);
    }
}