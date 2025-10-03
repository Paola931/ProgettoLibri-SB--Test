package ProgettoLibri_SB.Libro;

import ProgettoLibri_SB.Utente.Utente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Entity
@Table(name = "libro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libro", insertable = false, updatable = false)
    Long id_libro;
    @Column(nullable = false)
    String titolo;
    @Column(nullable = false)
    String autore;
    @Column(nullable = true)
    String copertina;
    @Column(nullable = true)
    double media_voti;
    @Column(nullable = false)
    String ce;

    @ManyToOne
    @JoinColumn(name = "id_utente", referencedColumnName = "id")
    @JsonIgnore
    private Utente utente;


}
