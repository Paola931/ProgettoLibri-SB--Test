package ProgettoLibri_SB.Amministratore;

import ProgettoLibri_SB.Utente.TipoUtenza;
import ProgettoLibri_SB.Utente.Utente;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
@DiscriminatorValue("AMMINISTRATORE")
public class Amministratore extends Utente {
    @Column(nullable = false)
    String ce;

    public Amministratore(Long id, String nome_utente, String email, String password, String immagine_profilo, TipoUtenza tipo_utenza, String ce) {
        super(id, nome_utente, email, password, immagine_profilo, tipo_utenza);
        this.ce = ce;
    }

}
