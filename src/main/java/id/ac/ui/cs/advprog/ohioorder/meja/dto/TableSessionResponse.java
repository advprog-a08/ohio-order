package id.ac.ui.cs.advprog.ohioorder.meja.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableSessionResponse {
    private String tableId;
    private String sessionId;
    private boolean isActive;
    private String message;
}
