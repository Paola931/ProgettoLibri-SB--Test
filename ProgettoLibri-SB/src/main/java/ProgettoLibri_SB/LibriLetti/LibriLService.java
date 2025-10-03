package ProgettoLibri_SB.LibriLetti;

import ProgettoLibri_SB.Libro.Libro;
import ProgettoLibri_SB.Libro.LibroRepository;
import ProgettoLibri_SB.Utente.TipoUtenza;
import ProgettoLibri_SB.Utente.Utente;
import ProgettoLibri_SB.Utente.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibriLService {
    @Autowired
    private LibriLRepository libriLRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private UtenteRepository utenteRepository;


    //aggiungi libri letti con voto per media voti
    public ResponseEntity aggiungiLibriLetti(Long idLettore, String titolo, String autore, int voto) {
        List<Libro> checkLibro = libroRepository.checkEsisteLibro(titolo, autore);
        Libro getIdLibro = libroRepository.checkidLibro(titolo, autore);
        TipoUtenza checkLettore = utenteRepository.checkUtenza(idLettore);
        if (checkLettore.equals(TipoUtenza.LETTORE)) {
            if (checkLibro.isEmpty()) {
                return ResponseEntity.status(404).body("Libro non presente");
            } else {

                if (libriLRepository.isCheckLibroLibriLetti(getIdLibro.getId_libro())) {
                    libriLRepository.aggiungiLibriLetti(idLettore, getIdLibro.getId_libro(), voto);
                    libroRepository.mediaVoto(getIdLibro.getId_libro());
                    return ResponseEntity.ok(checkLibro);
                }else{
                    return ResponseEntity.status(409).body("Libro già presente nella lista");
                }
            }
        } else {
            return ResponseEntity.status(401).body("Utente non aturizzato");
        }
    }
    //visualizza libri letti
    public ResponseEntity visualizzaLibriLetti(Long idUtente){
        Optional<Utente> checkUtente = utenteRepository.findById(idUtente);
        TipoUtenza checkUtenza = utenteRepository.checkUtenza(idUtente);
        List<Libro> libri = libriLRepository.visualizzaLibriL(idUtente);
        if(checkUtente.isPresent()){
            if(checkUtenza.equals(TipoUtenza.LETTORE)){
                return ResponseEntity.ok(libri);
            }else{
                return ResponseEntity.status(401).body("Non autorizzato");
            }
        }else {
            return ResponseEntity.status(404).body("Utente non trovato");
        }
    }
}
