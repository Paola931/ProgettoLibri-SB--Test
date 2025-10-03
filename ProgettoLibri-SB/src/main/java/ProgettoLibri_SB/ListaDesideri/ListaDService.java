package ProgettoLibri_SB.ListaDesideri;

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
public class ListaDService {
    @Autowired
    private ListaDRepository listaDRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    //aggiungi a lista desideri
    public ResponseEntity aggiungiListaDesideri(Long idLettore, String titolo, String autore) {
        List<Libro> checkLibro = libroRepository.checkEsisteLibro(titolo, autore);
        TipoUtenza checkLettore = utenteRepository.checkUtenza(idLettore);
        if (checkLettore.equals(TipoUtenza.LETTORE)) {
            if (checkLibro.isEmpty()) {
                return ResponseEntity.status(404).body("Libro non presente");
            } else {

                Libro idLibro = libroRepository.checkidLibro(titolo, autore);
                if (listaDRepository.isCheckLibroListaDesideri(idLibro.getId_libro())) {
                    listaDRepository.aggingiListaDesiseri(idLettore, idLibro.getId_libro());
                    return ResponseEntity.ok(checkLibro);
                } else {
                    return ResponseEntity.status(409).body("Libro già presente nella lista");
                }

            }
        } else {
            return ResponseEntity.status(401).body("Utente non aturizzato");
        }
    }
    //visualizza lista desderi
    public ResponseEntity visualizzaListaD(Long idUtente){
        Optional<Utente> utente = utenteRepository.findById(idUtente);
        TipoUtenza checkUtenza = utenteRepository.checkUtenza(idUtente);
        List<Libro> lista = listaDRepository.visualizzaListaD(idUtente);
        if(utente.isPresent()){
            if(checkUtenza.equals(TipoUtenza.LETTORE)) {
                return ResponseEntity.ok(lista);
            }else {
               return ResponseEntity.status(401).body("Non autorizzato");
            }
        }else{
           return ResponseEntity.status(404).body("Utente non trovato");
        }
    }
}
