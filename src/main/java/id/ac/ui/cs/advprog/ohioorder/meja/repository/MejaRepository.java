package id.ac.ui.cs.advprog.ohioorder.meja.repository;

import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MejaRepository extends JpaRepository<Meja, UUID> {
    Optional<Meja> findByNomorMeja(String nomorMeja);
    boolean existsByNomorMeja(String nomorMeja);
}