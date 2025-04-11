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
    void testFindByNomorMejaWhenExists() {
        Meja meja = Meja.builder()
                .nomorMeja("A1")
                .status(MejaStatus.TERSEDIA)
                .build();
        
        entityManager.persist(meja);
        entityManager.flush();

        Optional<Meja> found = mejaRepository.findByNomorMeja("A1");

        assertTrue(found.isPresent());
        assertEquals(meja.getId(), found.get().getId());
        assertEquals("A1", found.get().getNomorMeja());
    }

    @Test
    void testFindByNomorMejaWhenNotExists() {
        Optional<Meja> found = mejaRepository.findByNomorMeja("NonExistent");

        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByNomorMejaWhenExists() {
        Meja meja = Meja.builder()
                .nomorMeja("B2")
                .status(MejaStatus.TERSEDIA)
                .build();
        
        entityManager.persist(meja);
        entityManager.flush();

        assertTrue(mejaRepository.existsByNomorMeja("B2"));
    }

    @Test
    void testExistsByNomorMejaWhenNotExists() {
        assertFalse(mejaRepository.existsByNomorMeja("NonExistent"));
    }

    @Test
    void testSave() {
        Meja meja = Meja.builder()
                .nomorMeja("C3")
                .status(MejaStatus.TERSEDIA)
                .build();

        Meja savedMeja = mejaRepository.save(meja);

        assertNotNull(savedMeja.getId());
        assertEquals("C3", savedMeja.getNomorMeja());
    }

    @Test
    void testDelete() {
        Meja meja = Meja.builder()
                .nomorMeja("D4")
                .status(MejaStatus.TERSEDIA)
                .build();
        
        meja = entityManager.persist(meja);
        entityManager.flush();
        
        mejaRepository.delete(meja);
        entityManager.flush();
        
        Optional<Meja> found = mejaRepository.findById(meja.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
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

        Iterable<Meja> allMeja = mejaRepository.findAll();

        int count = 0;
        for (Meja meja : allMeja) {
            count++;
        }
        assertEquals(2, count);
    }
}