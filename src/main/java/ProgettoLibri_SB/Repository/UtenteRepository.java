package ProgettoLibri_SB.Repository;

import ProgettoLibri_SB.Entity.TipoUtenza;
import ProgettoLibri_SB.Entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    Optional<Utente> findByEmail(String email);

    Optional<Utente> findByEmailAndPassword(String email,String password);

    Optional<Utente> findByNomeUtente (String nomeUtente);

    TipoUtenza findTipoUtenzaById(Long id);


}
