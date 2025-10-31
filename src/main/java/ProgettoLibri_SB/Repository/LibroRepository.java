package ProgettoLibri_SB.Repository;

import ProgettoLibri_SB.Entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {



    Libro findLibroByTitoloAndAutore(String titolo,String autore);

    boolean findLibroByTitoloAndAutoreAndCopertinaIsNotNull(String titolo, String autore);

    Long findIdLibroByTitoloAndAutore(String titolo,String autore);

    double findAverageVotoByIdLibro(Long idLibro);

}

