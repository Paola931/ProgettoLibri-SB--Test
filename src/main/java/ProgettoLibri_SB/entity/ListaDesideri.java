package ProgettoLibri_SB.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Column(name = "id_lista_desideri")
Long id_lista_desideri;


    @ManyToOne
    @JoinColumn(name = "id_utenteld", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "id_librold", referencedColumnName = "id_libro", nullable = false)
    private Libro libro;

}
