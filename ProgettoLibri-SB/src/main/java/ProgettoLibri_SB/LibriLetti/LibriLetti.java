package ProgettoLibri_SB.LibriLetti;

import ProgettoLibri_SB.Libro.Libro;
import ProgettoLibri_SB.Utente.Utente;
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
    @Column(name = "id_libri_letti", insertable = false, updatable = false)
    Long id_libri_letti;
    @Column(name = "id_libro", insertable = false, updatable = false,nullable = false)
    Long id_libro;
    @Column(name = "id_utente", insertable = false, updatable = false, nullable = false)
    Long id_utente;
    @Column(nullable = true)
    int voto;


    @ManyToOne
    @JoinColumn(name = "id_utente", referencedColumnName = "id")
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "id_libro", referencedColumnName = "id_libro")
    private Libro libro;

}
