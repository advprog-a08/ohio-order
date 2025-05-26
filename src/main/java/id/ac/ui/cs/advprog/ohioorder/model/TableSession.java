package id.ac.ui.cs.advprog.ohioorder.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class TableSession {
    private String id;
    private String tableId;
    private String orderId;
    private Optional<String> checkoutId;
    private boolean isActive;
}
