package ProgettoLibri_SB.repository;

import ProgettoLibri_SB.entity.LibriLetti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
@Repository
public interface LibriLRepository extends JpaRepository<LibriLetti,Long> {

    List<LibriLetti> findByUtenteId (Long idUtente);
}

