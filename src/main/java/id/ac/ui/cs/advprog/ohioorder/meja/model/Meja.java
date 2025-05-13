package id.ac.ui.cs.advprog.ohioorder.meja.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "meja")
public class Meja {
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "nomor_meja", unique = true, nullable = false)
    private String nomorMeja;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MejaStatus status;
    
    public boolean isAvailable() {
        return status == MejaStatus.TERSEDIA;
    }
}