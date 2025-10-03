package ProgettoLibri_SB.Libro;

import jakarta.persistence.EntityResult;
import jakarta.persistence.FieldResult;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface LibroRepository extends JpaRepository<Libro,Long> {


    @Query(value = "SELECT * FROM libro WHERE titolo = :titolo AND autore = :autore ", nativeQuery = true)
    List<Libro> checkEsisteLibro(@Param("titolo") String titolo, @Param("autore") String autore);


    @Query(value = "SELECT id_libro FROM libro WHERE titolo LIKE '% :titolo %'  AND autore LIKE '% :autore %'", nativeQuery = true)
    Libro checkidLibro(@Param("titolo") String titolo, @Param("autore") String autore);

    @Query(value = "UPDATE  libro set media_voti = (select avg(voto)from libriletti where titolo = :titolo) where id_libro  = :id_libro;",nativeQuery = true)
    Libro mediaVoto (@Param("id_libro") Long id_libro);
}

