package id.ac.ui.cs.advprog.ohioorder.meja.repository;

import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MejaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MejaRepository mejaRepository;

    @Test
    void testFindByNomorMeja_WhenExists() {
        // Arrange
        Meja meja = Meja.builder()
                .nomorMeja("A1")
                .status(MejaStatus.TERSEDIA)
                .build();
        
        entityManager.persist(meja);
        entityManager.flush();

        // Act
        Optional<Meja> found = mejaRepository.findByNomorMeja("A1");

        // Assert
        assertTrue(found.isPresent());
        assertEquals(meja.getId(), found.get().getId());
        assertEquals("A1", found.get().getNomorMeja());
    }

    @Test
    void testFindByNomorMeja_WhenNotExists() {
        // Act
        Optional<Meja> found = mejaRepository.findByNomorMeja("NonExistent");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByNomorMeja_WhenExists() {
        // Arrange
        Meja meja = Meja.builder()
                .nomorMeja("B2")
                .status(MejaStatus.TERSEDIA)
                .build();
        
        entityManager.persist(meja);
        entityManager.flush();

        // Act & Assert
        assertTrue(mejaRepository.existsByNomorMeja("B2"));
    }

    @Test
    void testExistsByNomorMeja_WhenNotExists() {
        // Act & Assert
        assertFalse(mejaRepository.existsByNomorMeja("NonExistent"));
    }

    @Test
    void testSave() {
        // Arrange
        Meja meja = Meja.builder()
                .nomorMeja("C3")
                .status(MejaStatus.TERSEDIA)
                .build();

        // Act
        Meja savedMeja = mejaRepository.save(meja);

        // Assert
        assertNotNull(savedMeja.getId());
        assertEquals("C3", savedMeja.getNomorMeja());
    }

    @Test
    void testDelete() {
        // Arrange
        Meja meja = Meja.builder()
                .nomorMeja("D4")
                .status(MejaStatus.TERSEDIA)
                .build();
        
        meja = entityManager.persist(meja);
        entityManager.flush();
        
        // Act
        mejaRepository.delete(meja);
        entityManager.flush();
        
        // Assert
        Optional<Meja> found = mejaRepository.findById(meja.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        // Arrange
        Meja meja1 = Meja.builder()
                .nomorMeja("E5")
                .status(MejaStatus.TERSEDIA)
                .build();
        
        Meja meja2 = Meja.builder()
                .nomorMeja("F6")
                .status(MejaStatus.TERISI)
                .build();
        
        entityManager.persist(meja1);
        entityManager.persist(meja2);
        entityManager.flush();

        // Act
        Iterable<Meja> allMeja = mejaRepository.findAll();

        // Assert
        int count = 0;
        for (Meja meja : allMeja) {
            count++;
        }
        assertEquals(2, count);
    }
}