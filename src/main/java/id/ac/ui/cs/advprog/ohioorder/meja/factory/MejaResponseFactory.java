package id.ac.ui.cs.advprog.ohioorder.meja.factory;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import org.springframework.stereotype.Component;

@Component
public class MejaResponseFactory {
    
    public MejaResponse createFromEntity(Meja meja) {
        return MejaResponse.builder()
                .id(meja.getId())
                .nomorMeja(meja.getNomorMeja())
                .status(meja.getStatus())
                .build();
    }
    
    public MejaResponse createErrorResponse(String errorMessage) {
        return MejaResponse.builder()
                .nomorMeja(errorMessage)
                .build();
    }
}