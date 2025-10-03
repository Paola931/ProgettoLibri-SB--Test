package ProgettoLibri_SB.ListaDesideri;

import ProgettoLibri_SB.Libro.Libro;
import ProgettoLibri_SB.Utente.Utente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "listadesideri")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListaDesideri {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_lista_desideri", insertable = false, updatable = false)
Long id_lista_desideri;
@Column(name = "id_utente", insertable = false, updatable = false,nullable = false)
Long id_utente;
@Column(name = "id_libro", insertable = false, updatable = false, nullable = false)
Long id_libro;

    @ManyToOne
    @JoinColumn(name = "id_utente", referencedColumnName = "id")
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "id_libro", referencedColumnName = "id_libro")
    private Libro libro;

}
