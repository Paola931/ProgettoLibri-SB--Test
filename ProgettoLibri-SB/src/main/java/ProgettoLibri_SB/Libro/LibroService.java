package ProgettoLibri_SB.Libro;

import ProgettoLibri_SB.UploadDownload.UploadDownloadService;
import ProgettoLibri_SB.Utente.TipoUtenza;
import ProgettoLibri_SB.Utente.Utente;
import ProgettoLibri_SB.Utente.UtenteRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@org.springframework.stereotype.Service
public class LibroService {
    @Autowired
    private LibroRepository repository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private UploadDownloadService fileService;

    //cerca libro
    public ResponseEntity cercaLibro(String titolo, String autore) throws UnsupportedEncodingException {

        List<Libro> libri = repository.checkEsisteLibro(titolo, autore);

        if (libri.isEmpty()) {
            return ResponseEntity.status(404).body("Libro non presente");
        } else {
//            Libro libro = libri.get();
//            libro.getCopertina();

            return ResponseEntity.ok(libri);
        }
    }

    //media voti
    public ResponseEntity mediaVoto(Long idLibro) {
        Libro media = repository.mediaVoto(idLibro);
        if (repository.existsById(idLibro)) {
            return ok(media);
        } else {
            return ResponseEntity.status(404).body("Libro non presente");
        }
    }

    //aggiungi libro
    public ResponseEntity aggiungiLibro(Long id_utente, String titolo, String autore, String ce, MultipartFile copertina) {
        List<Libro> checkLibro = repository.checkEsisteLibro(titolo, autore);
        Optional<Utente> checkUtente = utenteRepository.findById(id_utente);
        if (checkUtente.isPresent() && utenteRepository.checkUtenza(id_utente).equals(TipoUtenza.AMMINISTRATORE)) {
            if (checkLibro.isEmpty()) {
                Libro libro = new Libro();
                libro.setTitolo(titolo);
                libro.setAutore(autore);
                libro.setCe(ce);
                String fileCopertina = fileService.upload(copertina);
                libro.setCopertina(fileCopertina);
                libro.setUtente(checkUtente.get());
                repository.saveAndFlush(libro);


                return ResponseEntity.ok(libro);
            } else {
                return ResponseEntity.status(409).body("Libro già presente");
            }
        } else {
            return ResponseEntity.status(401).body("Utente non autorizzato");
        }
    }

    //elimina libro
    public ResponseEntity eliminaLibro(Long idLibro, Long idAdmin, String titolo, String autore) {
        Optional<Utente> checkUtente = utenteRepository.findById(idAdmin);
        Libro libro = repository.checkidLibro(titolo, autore);
        boolean amministratore  = utenteRepository.checkUtenza(idAdmin).equals(TipoUtenza.AMMINISTRATORE);
        if (checkUtente.isPresent() && amministratore) {
            if (libro.getId_libro().equals(idLibro)) {
                repository.deleteById(idLibro);
                return ResponseEntity.ok(libro);
            } else {
                return ResponseEntity.status(400).body("Credenziali errate");
            }
        } else {
            return ResponseEntity.status(401).body("Utente non autorizzato");
        }
    }
}
