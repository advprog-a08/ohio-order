package id.ac.ui.cs.advprog.ohioorder.pesanan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemDto {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private Double price;
}