package ProgettoLibri_SB.ListaDesideri;

import ProgettoLibri_SB.Libro.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListaDRepository extends JpaRepository<Libro,Long> {

    @Query(value = "INSERT INTO listadesideri (id_utente, id_libro) VALUES (:idLettore, :idLibro)", nativeQuery = true)
    Libro aggingiListaDesiseri (@Param("idLettore") Long idLettore, @Param("idLibro") Long idLibro);

    @Query(value = "SELECT EXISTS (SELECT * FROM listadesideri WHERE id_libro = :idLibro) AS esiste",nativeQuery = true)
    boolean isCheckLibroListaDesideri(@Param("idLibro") Long idLibro);

    @Query(value = "SELECT l.id_libro, l.titolo, l.autore, l.media_voti, l.ce FROM libro  AS l JOIN listadesideri AS ld on ld.id_libro = l.id_libro WHERE ld.id_utente = :id_utente",nativeQuery = true)
    List<Libro> visualizzaListaD (@Param("id_utente") Long idUtente);
}
