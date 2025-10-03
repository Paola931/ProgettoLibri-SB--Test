package ProgettoLibri_SB.Lettore;

import ProgettoLibri_SB.Utente.TipoUtenza;
import ProgettoLibri_SB.Utente.Utente;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@DiscriminatorValue("LETTORE")
public class Lettore extends Utente {

    public Lettore(Long id, String nome_utente, String email, String password, String immagine_profilo, TipoUtenza tipo_utenza) {
        super(id, nome_utente, email, password, immagine_profilo, tipo_utenza);
    }
}
