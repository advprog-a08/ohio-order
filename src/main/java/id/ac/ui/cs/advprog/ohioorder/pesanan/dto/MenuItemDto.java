package id.ac.ui.cs.advprog.ohioorder.pesanan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDto {
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
}