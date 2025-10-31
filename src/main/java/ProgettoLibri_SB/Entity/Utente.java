package ProgettoLibri_SB.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(name = "nome_utente", nullable = false)
    String nomeUtente;
    @Column(nullable = false)
    String email;
    @Column(nullable = false)
    String password;
    String ce;
    @Lob
    String immagine_profilo;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TipoUtenza tipo_utenza;

}



