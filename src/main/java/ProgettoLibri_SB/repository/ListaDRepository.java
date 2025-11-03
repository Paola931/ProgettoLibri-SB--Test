package ProgettoLibri_SB.repository;


import ProgettoLibri_SB.entity.ListaDesideri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListaDRepository extends JpaRepository<ListaDesideri,Long> {

    List<ListaDesideri> findByUtenteId (Long idUtente);
}
