package id.ac.ui.cs.advprog.ohioorder.meja.dto;

import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MejaResponse {
    private UUID id;
    private String nomorMeja;
    private MejaStatus status;
}