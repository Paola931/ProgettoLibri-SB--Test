package ProgettoLibri_SB.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "libriletti")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibriLetti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libri_letti")
    Long id_libri_letti;
    @Column(nullable = false)
    int voto;


    @ManyToOne
    @JoinColumn(name = "id_utentell", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "id_libroll", referencedColumnName = "id_libro", nullable = false)
    private Libro libro;

}
