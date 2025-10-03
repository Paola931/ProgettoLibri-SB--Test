package ProgettoLibri_SB.Utente;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Entity
@Table(name = "utente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "tipo_utenza", discriminatorType = DiscriminatorType.STRING)
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    public  Long id;
    @Column(nullable = false)
    String nome_utente;
    @Column(nullable = false)
    String email;
    @Column(nullable = false)
    String password;
    @Lob
    @Column(nullable = true)
    String immagine_profilo;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TipoUtenza tipo_utenza;

}



