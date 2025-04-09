package id.ac.ui.cs.advprog.ohioorder.meja.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "meja")
public class Meja {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String nomorMeja;
    
    @Enumerated(EnumType.STRING)
    private MejaStatus status;
    
    public boolean isAvailable() {
        return status == MejaStatus.TERSEDIA;
    }
}