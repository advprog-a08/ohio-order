package id.ac.ui.cs.advprog.ohioorder.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TableSession {
    private String id;
    private String tableId;
    private boolean isActive;
}
