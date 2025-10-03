package ProgettoLibri_SB.LibriLetti;

import ProgettoLibri_SB.Libro.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LibriLRepository extends JpaRepository<Libro,Long> {

    @Query(value = "INSERT INTO libriletti (id_utente, id_libro, voto) VALUES (:idLettore, :idLibro, :voto)", nativeQuery = true)
    Libro aggiungiLibriLetti (@Param("idLettore") Long idLettore, @Param("idLibro") Long idLibro, @Param("voto") int voto);

    @Query(value = "SELECT EXISTS (SELECT * FROM libriletti WHERE id_libro = :idLibro) AS esiste",nativeQuery = true)
    boolean isCheckLibroLibriLetti(@Param("idLibro") Long idLibro);

    @Query(value = "SELECT l.id_libro, l.titolo, l.autore, l.media_voti, l.ce FROM libro  AS l JOIN libriletti AS ll on ll.id_libro = l.id_libro WHERE ll.id_utente = :id_utente",nativeQuery = true)
    List<Libro> visualizzaLibriL (@Param("idUtente") Long idUtente);
}

