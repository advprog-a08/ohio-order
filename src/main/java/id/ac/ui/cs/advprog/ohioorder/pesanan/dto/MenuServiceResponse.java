package id.ac.ui.cs.advprog.ohioorder.pesanan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuServiceResponse {
    private boolean success;
    private String message;
    private MenuItemDto data;
}