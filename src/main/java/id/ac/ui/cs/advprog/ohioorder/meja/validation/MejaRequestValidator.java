package id.ac.ui.cs.advprog.ohioorder.meja.validation;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaRequest;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.InvalidRequestException;
import id.ac.ui.cs.advprog.ohioorder.meja.repository.MejaRepository;
import id.ac.ui.cs.advprog.ohioorder.meja.utils.MejaConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MejaRequestValidator extends RequestValidator<MejaRequest> {

    private final MejaRepository mejaRepository;
    private final MejaConfig mejaConfig;
    
    @Override
    protected void doValidate(MejaRequest request) {
        validateNomorMejaNotEmpty(request);
        validateNomorMejaFormat(request);
        validateNomorMejaNotExists(request, null);
    }
    
    public void validateForUpdate(MejaRequest request, String currentNomorMeja) {
        validateNomorMejaNotEmpty(request);
        validateNomorMejaFormat(request);
        validateNomorMejaNotExists(request, currentNomorMeja);
    }
    
    private void validateNomorMejaNotEmpty(MejaRequest request) {
        if (request.getNomorMeja() == null || request.getNomorMeja().trim().isEmpty()) {
            throw new InvalidRequestException("Nomor meja tidak boleh kosong");
        }
    }
    
    private void validateNomorMejaFormat(MejaRequest request) {
        if (!mejaConfig.isValidTableNumber(request.getNomorMeja())) {
            throw new InvalidRequestException("Nomor meja tidak valid. Format yang benar adalah " + 
                    mejaConfig.getTablePrefix() + "X, dimana X adalah angka antara 1 dan " + mejaConfig.getMaxTableCount());
        }
    }
    
    private void validateNomorMejaNotExists(MejaRequest request, String currentNomorMeja) {
        boolean exists = mejaRepository.existsByNomorMeja(request.getNomorMeja());
        
        if (exists && (currentNomorMeja == null || !request.getNomorMeja().equals(currentNomorMeja))) {
            throw new InvalidRequestException("Meja dengan nomor " + request.getNomorMeja() + " sudah ada");
        }
    }
}