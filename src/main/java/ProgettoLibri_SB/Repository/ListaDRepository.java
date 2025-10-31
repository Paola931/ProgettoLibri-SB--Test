package ProgettoLibri_SB.Repository;


import ProgettoLibri_SB.Entity.ListaDesideri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListaDRepository extends JpaRepository<ListaDesideri,Long> {

    List<ListaDesideri> findByUtenteId (Long idUtente);
}
