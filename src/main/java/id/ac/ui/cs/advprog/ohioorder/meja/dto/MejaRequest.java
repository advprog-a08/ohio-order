package id.ac.ui.cs.advprog.ohioorder.meja.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MejaRequest {
    @NotBlank(message = "Nomor meja tidak boleh kosong")
    private String nomorMeja;
}