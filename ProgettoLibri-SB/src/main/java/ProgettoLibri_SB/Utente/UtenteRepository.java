package ProgettoLibri_SB.Utente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    Optional<Utente> findByEmail(String email);

    @Query(value = "SELECT * FROM utente WHERE nome_utente = :nomeUtente",nativeQuery = true)
    Optional<Utente> findByNomeUtente ( @Param("nomeUtente") String nomeUtente);

    @Query(value = " SELECT tipo_utenza FROM utente WHERE id = :id", nativeQuery = true)
    TipoUtenza checkUtenza(@Param("id") Long id);



}
