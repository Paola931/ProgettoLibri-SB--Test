package ProgettoLibri_SB.Service;

import ProgettoLibri_SB.Entity.TipoUtenza;
import ProgettoLibri_SB.Entity.Utente;
import ProgettoLibri_SB.Exception.UtenteException.UtenteGiaRegistratoException;
import ProgettoLibri_SB.Exception.UtenteException.UtenteNonTrovatoException;
import ProgettoLibri_SB.Repository.UtenteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AccessoRegistrazioneService {

    private final UtenteRepository utenteRepository;

    public AccessoRegistrazioneService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    public String registrazioneAdmin(Utente admin){
        Optional<Utente> esiste = utenteRepository.findByEmail(admin.getEmail());
        if(esiste.isEmpty()){
            Utente utenteEntity = new Utente();
            utenteEntity.setNomeUtente(admin.getNomeUtente());
            utenteEntity.setEmail(admin.getEmail());
            utenteEntity.setPassword(admin.getPassword());
            utenteEntity.setCe(admin.getCe());
            utenteEntity.setTipo_utenza(TipoUtenza.AMMINISTRATORE);
            utenteRepository.saveAndFlush(utenteEntity);
            return "Registrazione Amministratore effettuata con successo: "  + utenteEntity;
        }else {
            throw new UtenteGiaRegistratoException("Utente già registrato");
        }
    }

    public String registrazioneLettore(Utente lettore){
        Optional<Utente> esiste = utenteRepository.findByEmail(lettore.getEmail());
        if(esiste.isEmpty()){
            Utente utenteEntity = new Utente();
            utenteEntity.setNomeUtente(lettore.getNomeUtente());
            utenteEntity.setEmail(lettore.getEmail());
            utenteEntity.setPassword(lettore.getPassword());
            utenteEntity.setTipo_utenza(TipoUtenza.LETTORE);
            utenteRepository.saveAndFlush(utenteEntity);
            return "Registrazione Lettore effettuata con successo: "  + utenteEntity;
        }else {
            throw new UtenteGiaRegistratoException("Utente già registrato");
        }
    }

    public String accesso (String email, String password, HttpSession session){
        Optional<Utente> checkUtente = utenteRepository.findByEmailAndPassword(email, password);
        if(checkUtente.isPresent()){
            Utente utente = checkUtente.get();
            session.setAttribute("utentecorrente",utente);
            return "Benvenuto " + utente.getNomeUtente() + "!";
        }else {
            throw new UtenteNonTrovatoException("Utente non trovato");
        }
    }

    public String esci(HttpSession session) {
        session.invalidate();
        return "Logout effettuato";
    }
}
